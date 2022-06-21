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
		this.state = { dateFrom: new Date(), dateTo: new Date(), symbols: [], selectedSymbol: null }
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
		const { dateFrom, dateTo, symbols, selectedSymbol } = this.state
		const header = 'History'

		const optionKey = 'symbol'

		return (
			<div className='container'>
				<h1>{header}</h1>
				<form onSubmit={this.handleSave}>

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
								this.setState({ selectedSymbol: val })
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
