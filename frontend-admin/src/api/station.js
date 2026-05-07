import request from '@/utils/request'

export function fetchStationPage(params) {
  return request({
    url: '/station/page',
    method: 'get',
    params
  })
}

export function saveStation(data) {
  return request({
    url: '/station',
    method: 'post',
    data
  })
}

export function updateStation(data) {
  return request({
    url: '/station',
    method: 'put',
    data
  })
}

export function delStation(id) {
  return request({
    url: `/station/${id}`,
    method: 'delete'
  })
}

export function getStationById(id) {
  return request({
    url: `/station/${id}`,
    method: 'get'
  })
}

// 供其他模块下拉列表使用的全站检索
export function fetchAllStations() {
  return request({
    url: '/station/list',
    method: 'get'
  })
}
