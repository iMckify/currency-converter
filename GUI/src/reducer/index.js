import { combineReducers } from 'redux'
import { reducer as formReducer } from 'redux-form'
import apiReducer from './apiReducer'
import selectedReducer from './selectedReducer'

import watchlistsReducer from './watchlistsReducer'
import companiesReducer from './companiesReducer'

export default combineReducers({
	form: formReducer,

	apiReducer,
	selectedReducer,

	watchlistsReducer,
	companiesReducer,
})
