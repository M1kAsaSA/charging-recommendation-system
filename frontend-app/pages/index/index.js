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
      this.setData({ stations: res.data });
      wx.hideLoading();
    }).catch(() => {
      wx.hideLoading();
    });
  },
  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` });
  }
});