/* eslint-disable */
import React from 'react'
import PropTypes from 'prop-types'


export class HistoryForm extends React.Component {
	constructor(props) {
		super(props)
	}

	render() {
		const header = 'History'

		return (
			<div className='container-fluid'>
				<h1 className='row col mt-3'>{header}</h1>

				<div className='row'>
					<div className='col'>

					</div>
				</div>
			</div>
		)
	}
}

HistoryForm.propTypes = {
	history: PropTypes.object.isRequired,
}

export default HistoryForm
