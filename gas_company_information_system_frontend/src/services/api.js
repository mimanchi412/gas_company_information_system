import request from './request'

export const authApi = {
  sendCode: (email, purpose) => request.post('/auth/send-code', { email, purpose }),
  register: (payload) => request.post('/auth/register', payload),
  login: (payload) => request.post('/auth/login', payload),
  resetPassword: (payload) => request.post('/auth/reset-password', payload),
}

export const customerApi = {
  list: (keyword) => request.get('/customers', { params: { keyword } }),
  create: (payload) => request.post('/customers', payload),
  update: (id, payload) => request.put(`/customers/${id}`, payload),
  meters: (id) => request.get(`/customers/${id}/meters`),
  addMeter: (id, payload) => request.post(`/customers/${id}/meters`, payload),
}

export const billingApi = {
  recordReading: (payload) => request.post('/billing/readings', payload),
  createBill: (payload) => request.post('/billing/bills', payload),
  listBills: (params) => request.get('/billing/bills', { params }),
  payBill: (id, payload) => request.post(`/billing/bills/${id}/pay`, payload),
  arrears: () => request.get('/billing/arrears'),
}

export const workOrderApi = {
  create: (payload) => request.post('/work-orders', payload),
  assign: (id, payload) => request.patch(`/work-orders/${id}/assign`, payload),
  updateStatus: (id, payload) => request.patch(`/work-orders/${id}/status`, payload),
  list: (params) => request.get('/work-orders', { params }),
  detail: (id) => request.get(`/work-orders/${id}`),
}
