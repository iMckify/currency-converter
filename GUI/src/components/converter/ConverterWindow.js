import React from 'react'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'

function notEmpty(myString) {
    return myString !== ''
}

const styles = {
    title: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#3f51b5',
        textAlign: 'center',
    },
    paper: {
        position: 'absolute',
        boxShadow: '0 0 10px',
        padding: 32,
        outline: 'none',
        height: '80vh%',
        width: '20vw',
        top: '64px',
        left: `${40}%`,
        display: 'flex',
        flexDirection: 'column',
        boxSizing: 'initial',
    },
    textFields: {
        width: '17vw',
        height: '5vh',
        marginTop: '2vh',
        marginBottom: '2vh',
    },
    button: {
        marginTop: '2vh',
    },
    layout: {
        fontSize: 18,
        color: '#3f51b5',
        display: 'grid',
    },
}

class ConverterWindow extends React.Component {
    constructor(props) {
        super(props)

        this.state = { result: 0, amount: 0, amountError: '' }
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
            if (num < 0){
                errorText = `${name} can not be negative`
            }
        }

        const isError = notEmpty(errorText)
        if(isError) {
            this.setState({ [`${name}Error`]: errorText })
        }
    }

    handleSubmit = (e) => {
        e.preventDefault()
        const { amount, amountError } = this.state

        if (!notEmpty(amountError)) {
            // do something
        }
    }

    render() {
        const { amount, amountError } = this.state

        return (
            <form onSubmit={this.handleSubmit} style={styles.layout}>
                <TextField
                    style={styles.textFields}
                    name='amount'
                    label='Amount'
                    type='number'
                    onChange={this.handleChange}
                    onBlur={this.validate}
                    value={amount}
                    error={notEmpty(amountError)}
                    helperText={amountError}
                    margin='normal'
                />

                <Button
                    style={styles.button}
                    disabled={notEmpty(amountError)}
                    variant='outlined'
                    type='submit'
                >Calculate
                </Button>
            </form>
        )
    }
}

export default ConverterWindow