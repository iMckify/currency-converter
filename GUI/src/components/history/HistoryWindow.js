import React from 'react'
import BootstrapTable from 'react-bootstrap-table-next'
import filterFactory, { textFilter } from 'react-bootstrap-table2-filter'
import Chart from '../chart/Chart'
import axios from 'axios'
import {API} from '../../api/API'

const theadFormat = (
    column,
    colIndex,
    { sortElement, filterElement }
) => (
    <div style={{ display: 'grid' }}>
        <div>
            {column.text} {sortElement}
        </div>
        {filterElement}
    </div>
)

class HistoryWindow extends React.Component {
    constructor(props) {
        super(props)

        this.state = { quotes: [] }
    }

    componentDidMount() {
        const { ticker, dateFrom, dateTo } = this.props.match.params
        const symbol = ticker.replace('EUR','')

        const response = axios
            .get(`${API}/Forex/history/${symbol}/${dateFrom}/${dateTo}`)
            .then((res) => res.data)
            .then(data => {
                if (Array.isArray(data)) {
                    this.setState({ quotes: data.reverse() })
                }
            })
    }

    render() {
        const { ticker, dateFrom, dateTo } = this.props.match.params
        const { quotes } = this.state

        const header = `${ticker} (${dateFrom} to ${dateTo})`

        return (
            <div className='container-fluid'>
                <h1 className='row col mt-3'>{header}</h1>

                {quotes.length ? <Chart quotes={quotes} /> : null}
                <div className='row'>
                    <div className='col'>
                        <BootstrapTable
                            bootstrap4
                            data={quotes}
                            keyField='ticker'
                            bordered={false}
                            striped
                            hover
                            condensed
                            rowStyle={() => ({ lineHeight: 2.3 })}
                            filter={filterFactory()}
                            noDataIndication='No data'
                            columns={[
                                {
                                    dataField: 'date',
                                    text: 'Date',
                                    sort: true,
                                    filter: textFilter({ delay: 0 }),
                                    headerFormatter: theadFormat,
                                },
                                {
                                    dataField: 'value',
                                    text: 'Price',
                                    sort: true,
                                    filter: textFilter({ delay: 0 }),
                                    headerFormatter: theadFormat,
                                },
                            ]}
                        />
                    </div>
                </div>
            </div>
        )
    }
}

export default HistoryWindow