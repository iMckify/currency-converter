import toastr from 'toastr'
import * as ActionType from './ActionTypes'
import CrudApi from '../api/CrudApi'

//= =====================================================================================================================
//          COMMON
//= =====================================================================================================================
export const ApiCallBeginAction = () => ({
	type: ActionType.API_CALL_BEGIN,
})

export const ApiCallErrorAction = () => ({
	type: ActionType.API_CALL_ERROR,
})

export const clearSelected = () => ({
	type: ActionType.CLEAR_SELECTED,
	entity: {},
})

// action to clear selected entity from redux state after canceling in form
export function clearSelectedAction() {
	return (dispatch) => {
		dispatch(clearSelected())
	}
}

//= =====================================================================================================================
//          GETALL for investor
//= =====================================================================================================================
export const getAllForInvestorResponse = (entityName, entities) => ({
	type: ActionType.GET_CRUD_ALL_FOR_INVESTOR_RESPONSE(entityName),
	entities,
})

export function getCrudAllForInvestorAction(entityName, investorId) {
	return (dispatch) => {
		dispatch(ApiCallBeginAction())

		return CrudApi.getAllForInvestor(entityName, investorId)
			.then((entities) => {
				dispatch(getAllForInvestorResponse(entityName, entities))
			})
			.catch((error) => {
				toastr.error(error)
				throw error
			})
	}
}

//= =====================================================================================================================
//          GETALL
//= =====================================================================================================================
export const getAllResponse = (entityName, entities) => ({
	type: ActionType.GET_CRUD_ALL_RESPONSE(entityName),
	entities,
})

export function getCrudAllAction(entityName) {
	return (dispatch) => {
		dispatch(ApiCallBeginAction())

		return CrudApi.getAll(entityName)
			.then((entities) => {
				dispatch(getAllResponse(entityName, entities))
				return entities
			})
			.catch((error) => {
				toastr.error(error)
				throw error
			})
	}
}

//= =====================================================================================================================
//          CREATE/UPDATE then GETALL for investor
//= =====================================================================================================================
export const addCrudResponse = (entityName) => ({
	type: ActionType.ADD_CRUD_RESPONSE(entityName),
})

export const updateCrudResponse = (entityName) => ({
	type: ActionType.UPDATE_CRUD_RESPONSE(entityName),
})

export function saveCrudActionChain(entityName, entity, userId) {
	return function (dispatch) {
		dispatch(ApiCallBeginAction())

		return CrudApi.save(entityName, entity)
			.then(() => {
				if (entity.id) {
					dispatch(updateCrudResponse(entityName))
				} else {
					dispatch(addCrudResponse(entityName))
				}
			})
			.then(() => {
				dispatch(getCrudAllForInvestorAction(entityName, userId))
				toastr.success(
					`${entityName
						.split('/', 1)[0]
						.replace(/ies$/, 'y')
						.replace(/s$/, '')} saved`
				)
			})
			.catch((error) => {
				dispatch(ApiCallErrorAction())
				toastr.error(error)
				throw error
			})
	}
}

//= =====================================================================================================================
//          GET
//= =====================================================================================================================
export const getCrudResponse = (entityName, entity) => ({
	type: ActionType.GET_CRUD_RESPONSE(entityName),
	entity,
})

export function getCrudAction(entityName, entityId) {
	return (dispatch) => {
		dispatch(ApiCallBeginAction())

		return CrudApi.get(entityName, entityId)
			.then((entity) => {
				dispatch(getCrudResponse(entityName, entity))
				return entity
			})
			.catch((error) => {
				toastr.error(error)
				throw error
			})
	}
}

//= =====================================================================================================================
//          DELETE then GETALL for investor
//= =====================================================================================================================
export const deleteCrudResponse = (entityName) => ({
	type: ActionType.DELETE_CRUD_RESPONSE(entityName),
})

export function deleteCrudActionChain(entityName, entityId, investorId) {
	return (dispatch) => {
		dispatch(ApiCallBeginAction())

		return CrudApi.delete(entityName, entityId)
			.then(() => {
				dispatch(deleteCrudResponse(entityName))
			})
			.then(() => {
				dispatch(getCrudAllForInvestorAction(entityName, investorId))
			})
			.catch((error) => {
				toastr.error(error)
				throw error
			})
	}
}
