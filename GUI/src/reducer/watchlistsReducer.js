import _ from 'lodash'
import * as ActionType from '../actions/ActionTypes'
import initialState from './initialState'

const watchlistsReducer = (state = initialState.watchlistsReducer, action) => {
	switch (action.type) {
		case ActionType.GET_CRUD_ALL_FOR_INVESTOR_RESPONSE('Watchlists'): {
			return {
				...state,
				watchlists: _.assign(action.entities),
			}
		}

		default: {
			return state
		}
	}
}

export default watchlistsReducer
