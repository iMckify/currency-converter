/* eslint-disable */
import React from 'react'
import PropTypes from 'prop-types'
import DateBasic from '../common/DateBasic'


export class HistoryForm extends React.Component {
	constructor(props) {
		super(props)
		this.state= { date: ''}
	}

	handleSave = (x) => {
		console.log('handleSave')
	}

	handleChange = (x) => {
		console.log('handleChange')
		this.setState({ date: x })
	}

	render() {
		const header = 'History'

		return (
			<div className='container'>
				<h1>{header}</h1>
				<form onSubmit={this.handleSave}>
					<DateBasic
						name='date'
						label='Date'
						mode='past'
						required
						value={this.state.date}
						change={this.handleChange}
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
