/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useRef } from 'react'
import PropTypes from 'prop-types'
import { createChart, CrosshairMode } from 'lightweight-charts'
import { tradingViewDateToStr } from '../common/Utils'

export default function Chart(props) {
	const chartContainerRef = useRef(null)
	const chart = useRef(null)
	const resizeObserver = useRef(null)

	// eslint-disable-next-line
	useEffect(async () => {
		const { quotes } = props

		const priceData = quotes.map(forex => ({ time: forex.date, value: forex.value }))

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
			const data = quotes
			const price = data[data.length - 1]
			const dateStr = tradingViewDateToStr(price.time)
			toolTip.innerHTML = `<div style="font-size: 22px"> ${(
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
				const dateStr = tradingViewDateToStr(param.time)
				toolTip.innerHTML = `<div style="font-size: 22px"> ${(
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
	quotes: PropTypes.array.isRequired,
}
