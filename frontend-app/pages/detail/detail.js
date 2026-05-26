const { request } = require('../../utils/request.js');

Page({
  data: {
    stationId: null,
    stationName: '',
    stationAddress: '',
    stationLatitude: null,
    stationLongitude: null,
    markers: [],
    piles: [],
    loading: false
  },
  onLoad(options) {
    let name = options.name || '充电站';
    try {
      name = decodeURIComponent(name);
    } catch (e) {
      name = options.name || '充电站';
    }
    this.setData({ 
      stationId: options.id,
      stationName: name
    });
    this.fetchStationInfo();
    this.fetchPiles();
  },
  onPullDownRefresh() {
    this.fetchPiles().then(() => {
      wx.stopPullDownRefresh();
    });
  },
  fetchPiles() {
    this.setData({ loading: true });
    wx.showLoading({ title: '加载充电桩...' });
    return request('/api/v1/admin/pile/page', {
      data: { stationId: this.data.stationId, current: 1, size: 50 }
    }).then(res => {
      this.setData({ piles: res.data.records, loading: false });
      wx.hideLoading();
    }).catch(() => {
      this.setData({ loading: false });
      wx.hideLoading();
    });
  },
  fetchStationInfo() {
    if (!this.data.stationId) {
      return;
    }
    return request(`/api/v1/admin/station/${this.data.stationId}`, {
      method: 'GET'
    }).then(res => {
      const station = res.data || {};
      const latitude = station.latitude != null ? Number(station.latitude) : null;
      const longitude = station.longitude != null ? Number(station.longitude) : null;
      const markers = (latitude != null && longitude != null) ? [{
        id: station.id || 1,
        latitude,
        longitude,
        title: station.name || this.data.stationName,
        width: 24,
        height: 24
      }] : [];

      this.setData({
        stationName: station.name || this.data.stationName,
        stationAddress: station.address || '',
        stationLatitude: latitude,
        stationLongitude: longitude,
        markers
      });
    }).catch(() => {});
  },
  scanToStart() {
    wx.scanCode({
      onlyFromCamera: true,
      success: (res) => {
        const pileCode = (res.result || '').trim();
        if (!pileCode) {
          wx.showToast({ title: '未识别到充电桩编号', icon: 'none' });
          return;
        }
        const stationName = encodeURIComponent(this.data.stationName || '充电站');
        const stationAddress = encodeURIComponent(this.data.stationAddress || '');
        wx.navigateTo({
          url: `/pages/charging/charging?pileCode=${pileCode}` +
            `&stationName=${stationName}&stationAddress=${stationAddress}`
        });
      },
      fail: () => {
        wx.showToast({ title: '扫码已取消', icon: 'none' });
      }
    });
  },
  goToNavigation() {
    const { stationLatitude, stationLongitude, stationName, stationAddress } = this.data;
    if (stationLatitude == null || stationLongitude == null) {
      wx.showToast({ title: '缺少站点坐标', icon: 'none' });
      return;
    }
    const platform = wx.getDeviceInfo().platform;
    if (platform === 'devtools') {
      wx.showModal({
        title: '提示',
        content: '开发者工具不支持系统导航，请在真机测试。',
        showCancel: false
      });
      return;
    }
    wx.openLocation({
      latitude: Number(stationLatitude),
      longitude: Number(stationLongitude),
      name: stationName || '充电站',
      address: stationAddress || ''
    });
  },
  stopAndSettle() {
    wx.scanCode({
      onlyFromCamera: true,
      success: (res) => {
        const orderNo = (res.result || '').trim();
        if (!orderNo) {
          wx.showToast({ title: '未识别到订单编号', icon: 'none' });
          return;
        }
        wx.showLoading({ title: '结算中...' });
        request('/api/v1/app/order/stop', {
          method: 'POST',
          data: { orderNo }
        }).then(apiRes => {
          wx.hideLoading();
          const order = apiRes.data || {};
          const fee = order.totalAmount || 0;
          wx.showModal({ title: '结算完成', content: `本次消费：￥${Number(fee).toFixed(2)}`, showCancel: false });
          this.fetchPiles(); // 刷新状态
        }).catch(() => wx.hideLoading());
      },
      fail: () => {
        wx.showToast({ title: '扫码已取消', icon: 'none' });
      }
    });
  }
});