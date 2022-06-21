/* eslint-disable */
import React from 'react'
import PropTypes from 'prop-types'
import DateBasic from '../common/DateBasic'


export class HistoryForm extends React.Component {
	constructor(props) {
		super(props)
		this.state= { dateFrom: undefined}
	}

	handleSave = (x) => {
		console.log('handleSave')
	}

	handleChangeDateFrom = (date) => {
		this.setState({ dateFrom: date })
	}

	render() {
		const { dateFrom } = this.state
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
				</form>
			</div>
		)
	}
}

HistoryForm.propTypes = {
	history: PropTypes.object.isRequired,
}

export default HistoryForm
