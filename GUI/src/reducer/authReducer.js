import { LOGOUT, SET_CURRENT_USER } from '../actions/ActionTypes'
import { isEmpty } from '../validation'

const initialState = {
	isAuthenticated: false,
	user: {},
}

const authReducer = (state = initialState, action) => {
	switch (action.type) {
		case SET_CURRENT_USER:
			return {
				...state,
				isAuthenticated: !isEmpty(action.payload),
				user: action.payload,
			}
		case LOGOUT:
			return initialState
		default:
			return state
	}
}

export default authReducer
