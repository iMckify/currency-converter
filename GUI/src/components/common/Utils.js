export const dateToStr = (obj) => {
	let date = obj
	if (!date || date.toString() === 'Invalid Date') {
		date = new Date()
	} else if (typeof date === 'string' && !isNaN(Date.parse(date))) {
		date = new Date(date)
	}
	date.setHours(4) // todo
	date = date.toISOString().split('T')[0]
	return date
}

export const tradingViewDateToStr = (obj) => {
	let date = new Date()
	if (typeof obj === 'number') {
		date = new Date(obj * 1000)
	} else if (typeof obj === 'object') {
		date = new Date(obj.year, obj.month, obj.day)
	}
	date.setHours(4) // todo
	return date.toISOString().slice(0, 10)
}

export const isDateValid = (date) => {
	if (!date || date.toString() === 'Invalid Date') {
		return false
	}

	date.setHours(4)
	const dateStr = date.toISOString().split('T')[0]

	return (
		Boolean(dateStr) &&
		!isNaN(Date.parse(dateStr)) &&
		/^\d{4}-\d{2}-\d{2}$/.test(dateStr)
	)
}
