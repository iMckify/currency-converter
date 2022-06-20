export const API = 'http://localhost:8080/api'

export const headers = {
	'Access-Control-Allow-Origin': '*',
	'Access-Control-Allow-Credentials': 'true',
	'Access-Control-Allow-Methods':
		'ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL',
	'Access-Control-Max-Age': '3600',
	'Access-Control-Allow-Headers':
		'Origin, X-Requested-With, Content-Type, Accept, Key, Authorization',
}
