const { request } = require('../../utils/request.js');

Page({
  data: {
    stations: [],
    userLat: 31.2304, // 模拟用户纬度
    userLng: 121.4737 // 模拟用户经度
  },
  onLoad() {
    this.fetchRecommendations();
  },
  onPullDownRefresh() {
    this.fetchRecommendations().then(() => {
      wx.stopPullDownRefresh();
    });
  },
  fetchRecommendations() {
    wx.showLoading({ title: '正在计算推荐...' });
    return request('/api/v1/station/recommend', {
      method: 'POST',
      data: {
        longitude: this.data.userLng,
        latitude: this.data.userLat,
        expectedPrice: 1.5 // 模拟期望价格
      }
    }).then(res => {
      const stations = (res.data || []).map(item => ({
        ...item,
        priceText: this.formatPrice(item.currentPrice),
        distanceText: this.formatDistance(item.distance),
        scoreText: this.formatScore(item.recommendScore)
      }));
      this.setData({ stations });
      wx.hideLoading();
    }).catch(() => {
      wx.hideLoading();
    });
  },
  formatPrice(value) {
    const num = Number(value);
    if (Number.isNaN(num)) {
      return '1.20';
    }
    return num.toFixed(2);
  },
  formatScore(value) {
    const num = Number(value);
    if (Number.isNaN(num)) {
      return '0.00';
    }
    return num.toFixed(2);
  },
  formatDistance(value) {
    const num = Number(value);
    if (Number.isNaN(num)) {
      return '--';
    }
    if (num < 1) {
      return `${Math.round(num * 1000)} m`;
    }
    return `${num.toFixed(2)} km`;
  },
  goToDetail(e) {
    const item = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/detail/detail?id=${item.id}&name=${encodeURIComponent(item.name || '充电站')}` });
  },
  goToNavigation(e) {
    const { latitude, longitude, name, address } = e.currentTarget.dataset;
    if (latitude == null || longitude == null) {
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
      latitude: Number(latitude),
      longitude: Number(longitude),
      name: name || '充电站',
      address: address || '',
      success() {},
      fail() {
        wx.showToast({ title: '打开地图失败', icon: 'none' });
      }
    });
  }
});