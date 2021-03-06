/* eslint-disable */
import React from 'react'
import PropTypes from 'prop-types'
import Button from '@mui/material/Button'
import ShowChartIcon from '@mui/icons-material/ShowChart'
import TextField from '@mui/material/TextField'
import Autocomplete from '@mui/material/Autocomplete'
import DateBasic from '../common/DateBasic'
import CrudApi from '../../api/CrudApi'
import {dateToStr, isDateValid} from '../common/Utils'
import {styles} from "../../style/styles";


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
		const { dateFrom, dateTo, selectedSymbol } = this.state
		this.props.history.push(`/viewHistory/${selectedSymbol.symbol}/${dateToStr(dateFrom)}/${dateToStr(dateTo)}`)
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

		const areDatesValid = isDateValid(dateFrom) && isDateValid(dateTo)
		const isSubmitDisabled = (!areDatesValid || dateFrom?.getTime() > dateTo?.getTime()) || (isErrorSymbol || !selectedSymbol)

		return (
			<div className='container'>
				<h1>{header}</h1>
				<form onSubmit={this.handleSubmit} style={styles.layout}>

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
									style={styles.textFields}
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
						otherDate={dateTo}
						change={this.handleChangeDateFrom}
					/>

					<DateBasic
						name='dateTo'
						label='Date To'
						mode='past'
						required
						value={dateTo}
						otherDate={dateFrom}
						change={this.handleChangeDateTo}
					/>

					<div>
						<Button
							style={styles.button}
							type='submit'
							color='primary'
							variant='contained'
							disabled={ isSubmitDisabled }
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
