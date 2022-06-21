import { combineReducers } from 'redux'
import { reducer as formReducer } from 'redux-form'
import selectedReducer from './selectedReducer'

export default combineReducers({
	form: formReducer,

	selectedReducer,
})
