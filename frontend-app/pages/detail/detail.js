const { request } = require('../../utils/request.js');

Page({
  data: {
    stationId: null,
    piles: []
  },
  onLoad(options) {
    this.setData({ stationId: options.id });
    this.fetchPiles();
  },
  fetchPiles() {
    wx.showLoading({ title: '加载充电桩...' });
    // 为了省事，直接调管理端的查桩接口模拟
    request('/api/v1/admin/pile/page', {
      data: { stationId: this.data.stationId, current: 1, size: 50 }
    }).then(res => {
      this.setData({ piles: res.data.records });
      wx.hideLoading();
    }).catch(() => wx.hideLoading());
  },
  scanToStart() {
    wx.showModal({
      title: '扫描二维码 (模拟)',
      content: '请输入您要充电的充电桩编号 (Pile Code):',
      editable: true,
      placeholderText: '例如: P001',
      success: (res) => {
        if (res.confirm && res.content) {
          wx.showLoading({ title: '启动中...' });
          request('/api/v1/app/order/scan', {
            method: 'POST',
            data: { pileCode: res.content, userId: 1 } // 模拟用户ID为1
          }).then(apiRes => {
            wx.hideLoading();
            wx.showModal({ title: '提示', content: apiRes.msg || '充电已开始！', showCancel: false });
            this.fetchPiles(); // 刷新状态
          }).catch(() => wx.hideLoading());
        }
      }
    });
  },
  stopAndSettle() {
    wx.showModal({
      title: '结束充电 (模拟)',
      content: '请输入此次生成的订单ID (Order ID):',
      editable: true,
      success: (res) => {
        if (res.confirm && res.content) {
          wx.showLoading({ title: '结算中...' });
          request('/api/v1/app/order/stop', {
            method: 'POST',
            data: { orderId: res.content } 
          }).then(apiRes => {
            wx.hideLoading();
            const fee = apiRes.data.totalFee || 0;
            wx.showModal({ title: '结算完成', content: `本次消费：￥${fee.toFixed(2)}`, showCancel: false });
            this.fetchPiles(); // 刷新状态
          }).catch(() => wx.hideLoading());
        }
      }
    });
  }
});