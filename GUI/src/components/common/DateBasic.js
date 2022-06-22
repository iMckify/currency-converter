import React from 'react'
import PropTypes from 'prop-types'
import { DatePicker } from '@mui/x-date-pickers/DatePicker'
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import enLocale from 'date-fns/locale/en-US'
import TextField from '@mui/material/TextField'

function DateBasic(props) {
	const { name, required, mode, value, otherDate } = props
	let { label } = props
	label += ' (yyyy-mm-dd)'
	if (required) {
		label += ' *'
	}

	let minDate = new Date()
	if (mode === 'past') {
		minDate = new Date('2000-01-01')
	}

	if (name === 'dateTo' && !!otherDate) {
		minDate = otherDate
	}

	return (
		<div className='form-group'>
			<label htmlFor={name}>{label}</label>
			<div className='field'>
				<LocalizationProvider
					dateAdapter={AdapterDateFns}
					adapterLocale={enLocale}
				>
					<DatePicker
						reduceAnimations
						openTo='year'
						views={['year', 'month', 'day']}
						value={value || new Date()}
						disableFuture={mode === 'past'}
						minDate={minDate}
						onChange={(newDate, dateAsString) => {
							props.change(newDate)
						}}
						// inputFormat='dd/MM/yyyy'
						// mask='__/__/____' // default for inputFormat='dd/MM/yyyy'
						inputFormat='yyyy-MM-dd'
						mask='____-__-__'
						renderInput={(params) => {
							const dateStr = params?.inputProps?.value // always string or undefined, never Date object
							const isValid =
								Boolean(dateStr) &&
								!isNaN(Date.parse(dateStr)) &&
								/^\d{4}-\d{2}-\d{1,2}$/.test(dateStr)
							let isError = Boolean(params?.error)

							const currentDate = new Date(new Date().toLocaleDateString())
							currentDate.setHours(0)
							const date2000 = new Date('2000-01-01')
							date2000.setHours(0)

							// future date can be:  ok, invalid, < now
							// past date can be:    ok, invalid, < 2000, > now
							// past Date To can be: ok, < Date From
							let helperText = ' '
							if (mode === 'future') {
								if (!isValid) {
									isError = true
									helperText = 'Date is not valid'
								} else {
									const date = new Date(dateStr)
									date.setHours(4)

									if (date < currentDate) {
										isError = true
										helperText = 'Date must be today or future'
									}
								}
							}

							if (mode === 'past') {
								// const pattern = /^\d{4}-\d{2}-\d{2}$/
								// isValid = isValid && pattern.test(dateStr)
								helperText = ' '

								if (!isValid) {
									isError = true
									helperText = 'Date is not valid'
								} else {
									const date = new Date(dateStr)
									date.setHours(0)

									if (date < date2000) {
										isError = true
										helperText = 'Date must be after 2000-01-01'
									} else if (
										name === 'dateTo' &&
										!!otherDate &&
										date < otherDate
									) {
										isError = true
										helperText = `Date must be after 'Date From'`
									} else if (date > currentDate) {
										// ok
										isError = true
										helperText = 'Date must not be in the future'
									}
								}
							}

							return (
								<TextField
									{...params}
									id={name}
									name={name}
									error={isError}
									helperText={helperText}
									fullWidth
								/>
							)
						}}
					/>
				</LocalizationProvider>
			</div>
		</div>
	)
}

DateBasic.propTypes = {
	name: PropTypes.string.isRequired,
	required: PropTypes.bool,
	mode: PropTypes.string.isRequired,
	change: PropTypes.func.isRequired,
	label: PropTypes.string.isRequired,
	value: PropTypes.object,
	otherDate: PropTypes.object,
}

export default DateBasic
