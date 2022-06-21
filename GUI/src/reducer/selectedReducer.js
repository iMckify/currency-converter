import _ from 'lodash'
import * as ActionType from '../actions/ActionTypes'
import initialState from './initialState'

const selectedReducer = (state = initialState.selectedReducer, action) => {
	if (action.type.includes('GET') && !action.type.includes('ALL')) {
		return {
			...state,
			entity: _.assign(action.entity),
		}
	}
	if (action.type === ActionType.CLEAR_SELECTED) {
		return {}
	}
	return state
}

export default selectedReducer
