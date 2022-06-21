// This is to ensure that we can see the entirety of the store

const reducers = {
	apiReducer: {
		apiCallsInProgress: 0,
	},

	selectedReducer: {
		entity: undefined,
	},

	watchlistsReducer: {
		watchlists: [],
	},

	companiesReducer: {
		companies: [],
	},
}

export default reducers
