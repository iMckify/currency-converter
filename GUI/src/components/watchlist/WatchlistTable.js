import React from 'react'
import PropTypes from 'prop-types'
import Divider from '@mui/material/Divider'
import IconButton from '@mui/material/IconButton'
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline'
import BootstrapTable from 'react-bootstrap-table-next'
import Toolbar from '@mui/material/Toolbar'
import CrudApi from '../../api/CrudApi'

class WatchlistTable extends React.Component {
	constructor(props) {
		super(props)
		this.state = { selectedWatchlist: 'All', quotes: [] }
	}

	componentDidMount() {
		CrudApi.getAll('Forex/current')
			.then((entities) => {
				this.setState({ quotes: entities })
				return entities
			})
			.catch((error) => {
				throw error
			})
	}

	handleRemoveCompanyFromWatchlist = (e, row) => {
		const companiesID = row.id
		const { watchlistsID } = row

		// this.props.action.removeWatchlistCompanyActionChain(
		// 	'Watchlists',
		// 	companiesID,
		// 	watchlistsID,
		// 	// auth.user.id
		// )
	}

	removeFormatter = (cell, row, rowIndex, formatExtraData) => (
		<IconButton onClick={(e) => this.handleRemoveCompanyFromWatchlist(e, row)}>
			<RemoveCircleOutlineIcon />
		</IconButton>
	)

	render() {
		const { quotes } = this.state

		if (quotes.length === 0) {
			return <div>Loading...</div>
		}

		const columns = [
			{
				dataField: 'id',
				text: 'ID',
				hidden: true,
			},
			{
				dataField: 'symbol', // Todo selector (add multiple companies to watchlist)
				text: 'Ticker',
			},
			{
				dataField: 'value',
				text: 'Price',
			},
		]

		columns.push({
			dataField: 'remove',
			text: 'Remove',
			headerAttrs: {
				hidden: true,
			},
			formatter: this.removeFormatter,
		})


		return (
			<div>
				<Toolbar />

				<Divider light sx={{ borderBottomWidth: 8 }} />

				<Toolbar>
					<BootstrapTable
						bootstrap4
						keyField='id'
						bordered={false}
						hover
						condensed
						data={quotes}
						rowStyle={() => ({ lineHeight: 2.3 })}
						noDataIndication='No data'
						columns={columns}
					/>
				</Toolbar>
			</div>
		)
	}
}

WatchlistTable.propTypes = {
	history: PropTypes.object,
}

export default WatchlistTable
