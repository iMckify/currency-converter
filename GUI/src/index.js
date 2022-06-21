/* eslint-disable */
import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import configureStore from './reducer/configureStore'
import App from './components/App'
import './style/style.css'
import 'bootstrap/dist/js/bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css'

render(
    <Provider store={configureStore()}>
        <App />
    </Provider>,
    document.getElementById('root')
)
