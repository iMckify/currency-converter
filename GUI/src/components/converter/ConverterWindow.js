import React from 'react'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Autocomplete from '@mui/material/Autocomplete'
import CrudApi from '../../api/CrudApi'
import { styles } from '../../style/styles'

function notEmpty(myString) {
	return myString !== ''
}

class ConverterWindow extends React.Component {
	constructor(props) {
		super(props)

		this.state = {
			result: 0,
			symbols: [],
			symbolFrom: null,
			isErrorSymbolFrom: false,
			symbolTo: null,
			amount: 0,
			amountError: '',
		}
	}

	componentDidMount() {
		CrudApi.getAll('Forex/current')
			.then((entities) => {
				const symbols = entities.map((fx) => ({
					id: fx.id,
					value: fx.value,
					symbol: fx.symbol.replace('EUR', ''),
				}))
				symbols.unshift({ id: 0, value: 1, symbol: 'EUR' })
				this.setState({ symbols })
				return symbols
			})
			.catch((error) => {
				throw error
			})
	}

	handleChange = (e) => {
		const { name, value } = e.target
		this.setState({ [`${name}Error`]: '' })
		this.setState({ [name]: value })
	}

	validate = (e) => {
		const { name, value } = e.target
		let errorText = ''
		if (name === 'amount') {
			const num = parseFloat(value)
			if (num < 0) {
				errorText = `${name} can not be negative`
			}
		}

		const isError = notEmpty(errorText)
		if (isError) {
			this.setState({ [`${name}Error`]: errorText })
		}
	}

	validateSymbolFrom = (e) => {
		const { symbols } = this.state
		const { value } = e.target

		if (!symbols.map((fx) => fx.symbol).includes(value)) {
			this.setState({ isErrorSymbolFrom: true })
		}
	}

	validateSymbolTo = (e) => {
		const { symbols } = this.state
		const { value } = e.target

		if (!symbols.map((fx) => fx.symbol).includes(value)) {
			this.setState({ isErrorSymbolTo: true })
		}
	}

	handleSubmit = (e) => {
		const { amount, symbolFrom, symbolTo } = this.state
		e.preventDefault()

		CrudApi.getAll(
			`Forex/convert/${symbolFrom.symbol}/${symbolTo.symbol}/${amount}`
		)
			.then((result) => {
				this.setState({ result })
				return result
			})
			.catch((error) => {
				throw error
			})
	}

	render() {
		const {
			result,
			symbols,
			symbolFrom,
			isErrorSymbolFrom,
			symbolTo,
			isErrorSymbolTo,
			amount,
			amountError,
		} = this.state

		const header = 'LB Currency converter'
		const optionKey = 'symbol'

		const isSubmitDisabled =
			amount <= 0 ||
			isErrorSymbolFrom ||
			isErrorSymbolTo ||
			!symbolFrom ||
			!symbolTo

		return (
			<div className='container'>
				<h1>{header}</h1>
				<form onSubmit={this.handleSubmit} style={styles.layout}>
					<div className='form-group'>
						<label>From</label>
						<Autocomplete
							// autoSelect
							value={symbolFrom}
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
								this.setState({
									symbolFrom: val,
									isErrorSymbolFrom: false,
									result: 0,
								})
							}}
							renderInput={(params) => (
								<TextField
									{...params}
									variant='outlined'
									InputLabelProps={{ shrink: false }}
									label={symbolFrom?.[optionKey] ? ' ' : 'Search Symbol...'}
									sx={{
										'& label': {
											'&.Mui-focused': {
												visibility: 'hidden',
											},
										},
									}}
									style={styles.textFields}
									error={isErrorSymbolFrom}
									helperText={
										isErrorSymbolFrom ? 'Symbol is not selected' : ' '
									}
									onBlur={this.validateSymbolFrom}
								/>
							)}
						/>
					</div>

					<div className='form-group'>
						<label>To</label>
						<Autocomplete
							// autoSelect
							value={symbolTo}
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
								this.setState({
									symbolTo: val,
									isErrorSymbolTo: false,
									result: 0,
								})
							}}
							renderInput={(params) => (
								<TextField
									{...params}
									variant='outlined'
									InputLabelProps={{ shrink: false }}
									label={symbolTo?.[optionKey] ? ' ' : 'Search Symbol...'}
									sx={{
										'& label': {
											'&.Mui-focused': {
												visibility: 'hidden',
											},
										},
									}}
									style={styles.textFields}
									error={isErrorSymbolTo}
									helperText={isErrorSymbolTo ? 'Symbol is not selected' : ' '}
									onBlur={this.validateSymbolTo}
								/>
							)}
						/>
					</div>

					<div className='form-group'>
						<label>Amount</label>
						<div>
							<TextField
								name='amount'
								label=''
								type='number'
								onChange={this.handleChange}
								onBlur={this.validate}
								value={amount}
								error={notEmpty(amountError)}
								helperText={amountError}
								style={styles.textFields}
								sx={{
									'& label': {
										'&.Mui-focused': {
											visibility: 'hidden',
										},
									},
								}}
							/>
						</div>
					</div>

					<div className='form-group'>
						<label>Result</label>
						<div>
							<TextField
								name='result'
								label=''
								type='number'
								value={result}
								disabled
								style={styles.textFields}
								sx={{
									'& label': {
										'&.Mui-focused': {
											visibility: 'hidden',
										},
									},
								}}
							/>
						</div>
					</div>

					<Button
						style={styles.button}
						disabled={isSubmitDisabled}
						variant='outlined'
						type='submit'
					>
						Calculate
					</Button>
				</form>
			</div>
		)
	}
}

export default ConverterWindow
