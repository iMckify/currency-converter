import React from 'react'
import PropTypes from 'prop-types'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import Divider from '@mui/material/Divider'
import IconButton from '@mui/material/IconButton'
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline'
import BootstrapTable from 'react-bootstrap-table-next'
import filterFactory from 'react-bootstrap-table2-filter'
import Toolbar from '@mui/material/Toolbar'
import CircularProgress from '@mui/material/CircularProgress'
import _ from 'lodash'
import {
	deleteCrudActionChain,
	getCrudAllAction,
} from '../../actions/CrudActions'

class WatchlistTable extends React.Component {
	constructor(props) {
		super(props)
		this.state = { selectedWatchlist: 'All', quotes: [] }
	}

	componentDidMount() {
		this.props.action.getCrudAllAction('Forex/current')
			.then(rates => {
				this.setState({ quotes: rates })
			})
	}

	handleCreateNewWatchlist = () => {
		this.props.history.push('/watchlist/create')
	}

	handleDeleteWatchlist = () => {
		const { watchlists } = this.props
		const { selectedWatchlist } = this.state

		const wArr = watchlists.filter(
			(w) => w.name === selectedWatchlist && w.name !== 'All'
		)

		if (wArr.length) {
			this.setState({ selectedWatchlist: 'All' })
			// this.props.action.deleteCrudActionChain(
			// 	'Watchlists',
			// 	wArr[0].id,
			// 	// auth.user.id
			// )
		}
	}

	handleAddCompanyToWatchlist = () => {
		const { selectedWatchlist } = this.state
		const watchlist = this.props.watchlists.filter(
			(w) => w.name === selectedWatchlist
		)[0]
		this.props.history.push(`/watchlist/add/${watchlist.id}`)
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

	getMappedWatchlists = () => {
		const { watchlists } = this.props
		const { selectedWatchlist, quotes } = this.state

		if (!watchlists.length) {
			return []
		}

		return _.cloneDeep(watchlists)
			.filter(
				(value, index, self) =>
					selectedWatchlist === value.name || selectedWatchlist === 'All' // get selected
			)
			.flatMap((w) =>
				w.companies.map((c) => {
					const quote = quotes.length
						? quotes.find((q) => q.id === c.ticker)
						: undefined

					c.watchlistsID = w.id
					c.watchlistName = w.name
					c.investorsID = w.investorsID
					c.price = quote ? quote.price : undefined
					c.direction = quote ? quote.direction : ''
					return c
				})
			)
			.filter(
				(value, index, self) =>
					self.findIndex((value2) => value.ticker === value2.ticker) === index // removes duplicates
			)
	}

	render() {
		const { watchlists } = this.props
		const { selectedWatchlist, quotes } = this.state

		if (!watchlists) {
			return <div>Loading...</div>
		}

		const selectedWatchlistCompanies = this.getMappedWatchlists()

		if (!selectedWatchlistCompanies.every((c) => c.price)) {
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
						filter={filterFactory()}
						noDataIndication='No data'
						columns={columns}
					/>
				</Toolbar>
			</div>
		)
	}
}

WatchlistTable.propTypes = {
	watchlists: PropTypes.array,
	action: PropTypes.object.isRequired,
	history: PropTypes.object,
}

const mapStateToProps = (state) => ({
	watchlists: state.watchlistsReducer.watchlists,
})

const mapDispatchToProps = (dispatch) => ({
	action: bindActionCreators(
		{
			getCrudAllAction,
			deleteCrudActionChain,
		},
		dispatch
	),
})

export default connect(mapStateToProps, mapDispatchToProps)(WatchlistTable)
