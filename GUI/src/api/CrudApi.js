import axios from 'axios'
import { API } from './API'

class CrudApi {
	static getAll(entityName) {
		return axios.get(`${API}/${entityName}`).then((res) => res.data)
	}
}

export default CrudApi
