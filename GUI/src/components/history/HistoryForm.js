/* eslint-disable */
import React from 'react'
import PropTypes from 'prop-types'
import Button from '@mui/material/Button'
import ShowChartIcon from '@mui/icons-material/ShowChart'
import TextField from '@mui/material/TextField'
import Autocomplete from '@mui/material/Autocomplete'
import DateBasic from '../common/DateBasic'
import CrudApi from "../../api/CrudApi";


export class HistoryForm extends React.Component {
	constructor(props) {
		super(props)

		const today = new Date()
		today.setHours(4)

		const prevDay = new Date(today.getTime())
		prevDay.setDate(today.getDate() - 30)

		this.state = { dateFrom: prevDay, dateTo: today, symbols: [], selectedSymbol: null, isErrorSymbol: false }
	}

	componentDidMount() {
		CrudApi.getAll('Forex/current')
			.then((entities) => {
				this.setState({ symbols: entities })
				return entities
			})
			.catch((error) => {
				throw error
			})
	}

	handleSubmit = (e) => {
		const { dateFrom, dateTo } = this.state
		console.log('handleSubmit')
	}

	handleChangeDateFrom = (date) => {
		this.setState({ dateFrom: date })
	}

	handleChangeDateTo = (date) => {
		this.setState({ dateTo: date })
	}

	validateSymbol = (e) => {
		const { symbols } = this.state
		const { value } = e.target

		if (!symbols.map(fx => fx.symbol).includes(value)) {
			this.setState({ isErrorSymbol: true })
		}
	}

	render() {
		const { dateFrom, dateTo, symbols, selectedSymbol, isErrorSymbol } = this.state
		const header = 'History'

		const optionKey = 'symbol'

		return (
			<div className='container'>
				<h1>{header}</h1>
				<form onSubmit={this.handleSubmit}>

					<div className='form-group'>
						<label>Symbol</label>
						<Autocomplete
							// autoSelect
							value={selectedSymbol}
							loading={symbols.length === 0}
							options={symbols}
							getOptionLabel={(option) => option[optionKey]}
							renderOption={(props, option) => (
								<li {...props} style={{ justifyContent: 'space-between' }}>
									<div className='mr-5'>{option[optionKey]}</div>
									<div style={{ fontSize: 11, color: 'darkgray' }}>
										{option.value}
									</div>
								</li>
							)}
							onChange={(e, val) => {
								this.setState({ selectedSymbol: val, isErrorSymbol: false })
							}}
							style={{ width: 400 }}
							renderInput={(params) => (
								<TextField
									{...params}
									variant='outlined'
									InputLabelProps={{ shrink: false }}
									label={selectedSymbol?.[optionKey] ? ' ' : 'Search Symbol...'}
									sx={{
										'& label': {
											'&.Mui-focused': {
												visibility: 'hidden',
											},
										},
									}}
									error={isErrorSymbol}
									helperText={isErrorSymbol ? 'Symbol is not selected' : ' '}
									onBlur={this.validateSymbol}
								/>
							)}
						/>
					</div>

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
							disabled={ dateFrom.getTime() > dateTo.getTime() || isErrorSymbol }
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
