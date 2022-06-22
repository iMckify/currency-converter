/* eslint-disable no-unused-vars */
import React from 'react'
import { BrowserRouter, Route, Switch } from 'react-router-dom'

import NavBar from './NavBar'

import HomeWindow from './common/HomeWindow'
import HistoryForm from './history/HistoryForm'
import HistoryWindow from './history/HistoryWindow'

// eslint-disable-next-line react/prefer-stateless-function
class App extends React.Component {
	render() {
		const drawerWidth = 280

		return (
			<BrowserRouter>
				<NavBar drawerWidth={drawerWidth} />
				{/* conditional to trigger componentDidMount */}
				<div
					style={{
						// flexGrow: 1,
						marginTop: 72,
						marginRight: drawerWidth,
					}}
				>
					{/* Todo width for tables and columns */}
					<Switch>
						<Route exact path='/' component={HomeWindow} />

						<Route path='/history' component={HistoryForm} />
						<Route path='/viewHistory/:ticker/:dateFrom/:dateTo' component={HistoryWindow} />
					</Switch>
				</div>
			</BrowserRouter>
		)
	}
}

export default App
