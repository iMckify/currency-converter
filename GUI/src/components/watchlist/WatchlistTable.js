import React from 'react'
import PropTypes from 'prop-types'
import Divider from '@mui/material/Divider'
import IconButton from '@mui/material/IconButton'
import ShowChartIcon from '@mui/icons-material/ShowChart';
import BootstrapTable from 'react-bootstrap-table-next'
import Toolbar from '@mui/material/Toolbar'
import CircularProgress from '@mui/material/CircularProgress'
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

	handleOpenChart = (e, row) => {
		const { symbol } = row
		this.props.history.push(`/chart/${symbol}`)
	}

	formatter = (cell, row, rowIndex, formatExtraData) => (
		<IconButton onClick={(e) => this.handleOpenChart(e, row)}>
			<ShowChartIcon />
		</IconButton>
	)

	render() {
		const { quotes } = this.state

		if (quotes.length === 0) {
			return <CircularProgress sx={{ margin: 'auto' }} size={100} />
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
			formatter: this.formatter,
		})

		// const mappedQuotes = quotes.map(q => ({...q, value: Math.round(q.value.toFixed(6) * 100000) / 100000}))

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
