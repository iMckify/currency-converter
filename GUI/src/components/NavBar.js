/* eslint-disable  jsx-a11y/no-static-element-interactions, react/no-array-index-key */
import React from 'react'
import PropTypes from 'prop-types'
import { withRouter, NavLink } from 'react-router-dom'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import IconButton from '@mui/material/IconButton'
import MenuItem from '@mui/material/MenuItem'
import Menu from '@mui/material/Menu'
import MoreIcon from '@mui/icons-material/MoreVert'
import ListItemIcon from '@mui/material/ListItemIcon'
import Drawer from '@mui/material/Drawer'

class NavBar extends React.Component {
	constructor(props) {
		super(props)

		this.state = {
			mobileMoreAnchorEl: null,
		}
	}

	componentDidMount() {
		this.getAll() // initial
		this.interval = setInterval(() => {
			this.getAll()
		}, 10 * 60 * 1000) // every 10 min
	}

	componentWillUnmount() {
		clearInterval(this.interval)
	}

	getAll = () => {
		// const { action, auth } = this.props
		//
		// action
		// 	.getCrudAllForInvestorAction('Notifications/produce', auth.user.id) // both api url and action.type
	}

	openMobileM = (event) => {
		this.setState({ mobileMoreAnchorEl: event.currentTarget })
	}

	closeMobileM = () => {
		this.setState({ mobileMoreAnchorEl: null })
	}

	render() {
		const { history, drawerWidth } = this.props
		const { mobileMoreAnchorEl } = this.state
		const isMobileMenuOpen = Boolean(mobileMoreAnchorEl)

		const mobileMenuId = 'mobile-menu'

		const navContent = [
			{
				name: 'Home',
				to: '/',
			},
		]

		const navInLine = navContent.map((obj, i) => {
			if (obj.component) {
				return (
					<div
						key={i}
						onClick={(e) =>
							obj.clickFun ? obj.clickFun(e) : history.push(obj.to)
						}
					>
						{obj.component}
					</div>
				)
			}
			if (!obj.role) {
				return (
					<NavLink
						key={i}
						className={(isActive) =>
							`nav-item nav-link navLink${!isActive ? ' unselected' : ''}`
						}
						style={{ marginTop: 5 }}
						activeStyle={{ color: 'white' }}
						activeClassName='active'
						to={obj.to}
						isActive={(match, loc) => location.pathname === obj.to}
					>
						{obj.name}
					</NavLink>
				)
			}
			return null
		})

		const navInMobileMenu = navContent.map((obj, i) => (
			// eslint-disable-next-line react/no-array-index-key
			<MenuItem
				key={i}
				onClick={(e) => {
					if (obj.clickFun) {
						obj.clickFun(e)
					} else {
						history.push(obj.to)
					}

					if (obj.name !== 'Profile') {
						this.closeMobileM()
					}
				}}
			>
				{obj.name ? obj.name : null}
				{obj.component ? <ListItemIcon>{obj.component}</ListItemIcon> : null}
			</MenuItem>
		))

		return (
			<div>
				{/* icons in line */}
				{/* =================	currently resizes navbar and window on load		========================== */}
				<AppBar
					// position='static' // right space, switch does not need marginTop
					// position='absolute' // responsively cut, need style
					position='fixed' // recommended for desktop
					// style={{ width: `calc(100% - ${drawerWidth}px)` }}
				>
					<Toolbar>
						{/* icons in line */}
						<Box
							id='navBox'
							className='navHeight'
							sx={{
								display: { xs: 'none', md: 'none', lg: 'flex' },
							}}
						>
							{navInLine}
						</Box>
						{/* Mobile. more icon */}
						<Box sx={{ display: { xs: 'flex', md: 'flex', lg: 'none' } }}>
							<IconButton
								size='large'
								aria-label='show more'
								aria-controls={mobileMenuId}
								aria-haspopup='true'
								onClick={this.openMobileM}
								color='inherit'
							>
								<MoreIcon />
							</IconButton>
						</Box>
					</Toolbar>
				</AppBar>
				<Drawer
					variant='permanent'
					anchor='right'
					sx={{
						width: drawerWidth,
						flexShrink: 0,
						'& .MuiDrawer-paper': {
							width: drawerWidth,
							boxSizing: 'border-box',
						},
					}}
				>
					{/* <WatchlistTable history={this.props.history} /> */}
				</Drawer>
				{/* Modal. mobile */}
				<div>
					<Menu
						anchorEl={mobileMoreAnchorEl}
						anchorOrigin={{
							vertical: 'top',
							horizontal: 'left',
						}}
						id={mobileMenuId}
						keepMounted
						transformOrigin={{
							vertical: 'top',
							horizontal: 'left',
						}}
						open={isMobileMenuOpen}
						onClose={this.closeMobileM}
					>
						{navInMobileMenu}
					</Menu>
				</div>
			</div>
		)
	}
}

NavBar.propTypes = {
	// eslint-disable-next-line react/no-unused-prop-types
	history: PropTypes.object.isRequired,
	drawerWidth: PropTypes.number.isRequired,
}

export default withRouter(NavBar)
