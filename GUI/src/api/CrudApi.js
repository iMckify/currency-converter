import axios from 'axios'
import { API } from './API'

class CrudApi {
	static getAllForInvestor(entityName, investorId) {
		return axios
			.get(`${API}/${entityName}/investor/${investorId}`)
			.then((res) => res.data)
	}

	static getAll(entityName) {
		return axios.get(`${API}/${entityName}`).then((res) => res.data)
	}

	static save(entityName, entity) {
		if (entity.id) {
			return axios
				.post(`${API}/${entityName}/update/${entity.id}`, entity)
				.then((res) => res.data)
		}
		return axios
			.post(`${API}/${entityName}/create`, entity)
			.then((res) => res.data)
	}

	static delete(entityName, id) {
		return axios.delete(`${API}/${entityName}/delete/${id}`)
	}

	static get(entityName, id) {
		return axios.get(`${API}/${entityName}/${id}`).then((res) => res.data)
	}

	// Administrator unapproved requests
	static getRequestsAllForAdministratorUnapproved() {
		return axios.get(`${API}/Requests/unapproved`).then((res) => res.data)
	}

	// used 1 time for Notifications mark
	static post(entityName, id) {
		return axios.post(`${API}/${entityName}/${id}`).then((res) => res.data)
	}

	// used 1 time for WatchlistCompanies
	static add(entityName, entity) {
		return axios
			.post(`${API}/${entityName}/add`, entity)
			.then((res) => res.data)
	}

	// used for Watchlists create/update
	static saveByName(entityName, currentWatchlistName, entity) {
		if (currentWatchlistName) {
			return axios
				.post(`${API}/${entityName}/update/${currentWatchlistName}`, entity)
				.then((res) => res.data)
		}
		return axios
			.post(`${API}/${entityName}/create`, entity)
			.then((res) => res.data)
	}

	static removeWatchlistCompany(entityName, companiesID, watchlistsID) {
		return axios.delete(
			`${API}/${entityName}/remove/${companiesID}/${watchlistsID}`
		)
	}
}

export default CrudApi
