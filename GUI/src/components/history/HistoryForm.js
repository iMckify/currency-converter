/* eslint-disable */
import React from 'react'
import PropTypes from 'prop-types'
import Button from '@mui/material/Button'
import ShowChartIcon from '@mui/icons-material/ShowChart'
import DateBasic from '../common/DateBasic'


export class HistoryForm extends React.Component {
	constructor(props) {
		super(props)
		this.state = { dateFrom: new Date(), dateTo: new Date() }
	}

	handleSave = (e) => {
		const { dateFrom, dateTo } = this.state
		console.log('handleSave')
	}

	handleChangeDateFrom = (date) => {
		this.setState({ dateFrom: date })
	}

	handleChangeDateTo = (date) => {
		this.setState({ dateTo: date })
	}

	render() {
		const { dateFrom, dateTo } = this.state
		const header = 'History'

		return (
			<div className='container'>
				<h1>{header}</h1>
				<form onSubmit={this.handleSave}>
					<DateBasic
						name='dateFrom'
						label='Date From'
						mode='past'
						required
						value={dateFrom}
						change={this.handleChangeDateFrom}
					/>

					<DateBasic
						name='dateTo'
						label='Date To'
						mode='past'
						required
						value={dateTo}
						change={this.handleChangeDateTo}
					/>

					<div>
						<Button
							type='submit'
							color='primary'
							variant='contained'
							disabled={ dateFrom.getTime() > dateTo.getTime() }
							startIcon={<ShowChartIcon style={{ marginRight: -5 }} />}
						>
							View Prices
						</Button>
					</div>
				</form>
			</div>
		)
	}
}

HistoryForm.propTypes = {
	history: PropTypes.object.isRequired,
}

export default HistoryForm
