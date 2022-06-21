import _ from 'lodash'
import * as ActionType from '../actions/ActionTypes'
import initialState from './initialState'

const companiesReducer = (state = initialState.companiesReducer, action) => {
	switch (action.type) {
		case ActionType.GET_CRUD_ALL_RESPONSE('Companies'): {
			return {
				...state,
				companies: _.assign(action.entities),
			}
		}

		default: {
			return state
		}
	}
}

export default companiesReducer
