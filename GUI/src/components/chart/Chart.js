/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useRef } from 'react'
import PropTypes from 'prop-types'
import { createChart, CrosshairMode } from 'lightweight-charts'
import axios from 'axios'
import _ from 'lodash'
import { API } from '../../api/API'

export const dateToStrSimple = (obj) => {
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

const dateToStr = (obj) => {
	let date = new Date()
	if (typeof obj === 'number') {
		date = new Date(obj * 1000)
	} else if (typeof obj === 'object') {
		date = new Date(obj.year, obj.month, obj.day)
	}
	date.setHours(4) // todo
	return date.toISOString().slice(0, 10)
}

export default function Chart(props) {
	const chartContainerRef = useRef(null)
	const chart = useRef(null)
	const resizeObserver = useRef(null)

	// eslint-disable-next-line
	useEffect(async () => {
		const today = new Date()
		today.setHours(4)

		const dateTo = dateToStrSimple(today)

		today.setDate(today.getDate() - 30)
		const dateFrom = dateToStrSimple(today)

		const ticker = props.match.params.symbol
		const symbol = ticker.replace('EUR','')

		const response = await axios
			.get(`${API}/Forex/history/${symbol}/${dateFrom}/${dateTo}`)
			.then((res) => res.data)
		const priceData = response.reverse().map(forex => ({ time: forex.date, value: forex.value }))

		chart.current = createChart(chartContainerRef.current, {
			width: 600,
			height: 300,
			layout: {
				backgroundColor: '#FFFFFF',
				textColor: '#333',
			},
			grid: {
				horzLines: {
					color: '#eee',
				},
				vertLines: {
					color: '#eee',
				},
			},
			crosshair: {
				mode: CrosshairMode.Magnet,
			},
			priceScale: {
				// autoScale: true,
				borderVisible: false,
			},
			timeScale: {
				borderVisible: false,
			},
		})

		const areaSeries = chart.current.addAreaSeries({
			topColor: 'rgba(38,198,218, 0.56)',
			bottomColor: 'rgba(38,198,218, 0.04)',
			lineColor: 'rgba(38,198,218, 1)',
			lineWidth: 2,
		})

		areaSeries.setData(priceData)

		const container = document.querySelector('#chart')
		const toolTip = document.createElement('div')
		toolTip.className = 'three-line-legend'
		container.appendChild(toolTip)

		function setLastBarText() {
			const data = response
			const price = data[data.length - 1]
			const dateStr = dateToStr(price.time)
			toolTip.innerHTML = `<div style="font-size: 22px"> $${(
				Math.round(price.value * 100) / 100
			).toFixed(2)} </div>
				<div> ${dateStr} </div>`
		}
		setLastBarText()

		chart.current.subscribeCrosshairMove((param) => {
			if (
				param.point === undefined ||
				!param.time ||
				param.point.x < 0 ||
				param.point.x > container.clientWidth ||
				param.point.y < 0 ||
				param.point.y > container.clientHeight
			) {
				setLastBarText()
			} else {
				const price = param.seriesPrices.get(areaSeries)
				const dateStr = dateToStr(param.time)
				toolTip.innerHTML = `<div style="font-size: 22px"> $${(
					Math.round(price * 100) / 100
				).toFixed(2)} </div>
				<div> ${dateStr} </div>`
			}
		})

		// Resize chart on container resizes.
		resizeObserver.current = new ResizeObserver((entries) => {
			const { width, height } = entries[0].contentRect
			chart.current.applyOptions({ width, height })
			chart.current.timeScale().fitContent()
			setTimeout(() => {
				chart.current.timeScale().fitContent()
			}, 0)
		})

		resizeObserver.current.observe(chartContainerRef.current)

		// eslint-disable-next-line
		return () => resizeObserver.current.disconnect()
	}, [])

	return (
		<div id='chart' className='chart-container'>
			<div
				ref={chartContainerRef}
				className='ChartComponent'
				style={{ minWidth: 200 }}
			/>
		</div>
	)
}

Chart.propTypes = {
	match: PropTypes.object.isRequired,
}
