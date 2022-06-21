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
		this.state = { dateFrom: new Date(), dateTo: new Date(), symbols: [], symbol: 'EURUSD' }
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
		const { dateFrom, dateTo, symbols, symbol } = this.state
		const header = 'History'

		const optionKey = 'symbol'

		return (
			<div className='container'>
				<h1>{header}</h1>
				<form onSubmit={this.handleSave}>
					<div className='form-group'>
						<label>Company</label>
						<Autocomplete
							autoSelect
							value={symbol}
							loading={symbols.length === 0}
							options={symbols}
							getOptionLabel={(option) => {
								if (option !== '') {
									return `${option[optionKey]} | ${option.value}`
								}
								return ''
							}}
							renderOption={(props, option) => (
								<li {...props} style={{ justifyContent: 'space-between' }}>
									<div className='mr-5'>{option[optionKey]}</div>
									<div style={{ fontSize: 11, color: 'darkgray' }}>
										{option.value}
									</div>
								</li>
							)}
							onChange={(e, val) => {
								this.setState({ symbol: val.symbol })
							}}
							isOptionEqualToValue={(option, val) => {
								if (option[optionKey] || val[optionKey]) {
									return (
										option[optionKey] === val[optionKey] ||
										option[optionKey] === symbol
									)
								}
								return option === val || option === symbol
							}}
							style={{ width: 400 }}
							renderInput={(params) => (
								<TextField
									{...params}
									variant='outlined'
									InputLabelProps={{ shrink: false }}
									label={symbol || params.inputProps.value ? ' ' : 'Symbol'}
									sx={{
										'& label': {
											'&.Mui-focused': {
												visibility: 'hidden',
											},
										},
										marginBottom: `${1}rem`,
									}}
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
