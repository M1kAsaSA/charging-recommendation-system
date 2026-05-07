import request from '@/utils/request'

export function fetchPilePage(params) {
  return request({
    url: '/pile/page',
    method: 'get',
    params
  })
}

export function savePile(data) {
  return request({
    url: '/pile',
    method: 'post',
    data
  })
}

export function updatePile(data) {
  return request({
    url: '/pile',
    method: 'put',
    data
  })
}

export function delPile(id) {
  return request({
    url: `/pile/${id}`,
    method: 'delete'
  })
}
