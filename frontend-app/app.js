App({
  onLaunch() {
    console.log('App Launch')
  },
  globalData: {
    // devtools uses localhost; real device uses LAN IP
    baseUrl: (wx.getDeviceInfo().platform === 'devtools')
      ? 'http://127.0.0.1:8081'
      : 'http://192.168.110.13:8081'
  }
})