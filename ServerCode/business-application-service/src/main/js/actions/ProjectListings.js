const {
  RETRIEVE_PROJECT_LISTINGS,
  ALL_LISTINGS,
  GET_PROJECT_LISTING_BY_USERNAME,
  CREATE_PROJECT_LISTING,
  CREATE_PROJECT_LISTING_BY_USERNAME,
  UPDATE_PROJECT_LISTING,
  DELETE_ALL_PROJECT_LISTINGS,
  DELETE_PROJECT_LISTINGS_BY_USER,
  DELETE_PROJECT_BY_ID,
} = require("./Types");

const {
  createProjectListingByUserID,
  getProjectListingsByUserID,
  removeProjectListingById,
  getAllProjectListings,
  reviewProjectListingByID,
  addValueEstimation,
  editProjectListing,
  rejectListingById,
  assignRestorationById,
  postItemForSaleById,
  startAuctionById,
  finishRestorationById,
} = require("../services/ProjectListingsService");

// async create
const createProjectListingByUserIDAction =
  (userID, data) => async (dispatch) => {
    try {
      const res = await createProjectListingByUserID(userID, data);

      dispatch({
        type: CREATE_PROJECT_LISTING,
        payload: res.data,
      });

      return Promise.resolve(res.data);
    } catch (err) {
      return Promise.reject(err);
    }
  };

// create (no network call)
const addNewProjectListing = (data) => (dispatch) => {
  dispatch({
    type: CREATE_PROJECT_LISTING,
    payload: data,
  });
};

const getListingsPendingApproval = () => async (dispatch) => {
  try {
    // TBD replace with correct fetch
    const res = await getAllProjectListings();

    dispatch({
      type: ALL_LISTINGS,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const getAllProjectListingsAction = () => async (dispatch) => {
  try {
    const res = await getAllProjectListings();

    dispatch({
      type: ALL_LISTINGS,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const approveProject = (listingId) => async (dispatch) => {
  try {
    const res = await approveProjectListingByID(listingId);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const addValueEstimationAction = (listingId, data) => async (dispatch) => {
  try {
    const res = await addValueEstimation(listingId, data);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const assignRestorationByIdAction = (listingId) => async (dispatch) => {
  try {
    const res = await assignRestorationById(listingId);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const finishRestorationByIdAction = (listingId) => async (dispatch) => {
  try {
    const res = await finishRestorationById(listingId);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const postItemForSaleByIdAction = (listingId) => async (dispatch) => {
  try {
    const res = await postItemForSaleById(listingId);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const startAuctionByIdAction = (listingId) => async (dispatch) => {
  try {
    const res = await startAuctionById(listingId);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const getProjectListingsByUserIDAction =
  (userId, pageSize, pageNumber) => async (dispatch) => {
    try {
      console.log("userId", userId);
      const res = await getProjectListingsByUserID(
        userId,
        pageSize,
        pageNumber
      );

      dispatch({
        type: ALL_LISTINGS,
        payload: res.data,
      });

      return Promise.resolve(res.data);
    } catch (err) {
      return Promise.reject(err);
    }
  };

const editProjectListingById = (listingId, data) => async (dispatch) => {
  try {
    const res = await editProjectListing(listingId, data);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const deleteProjectByIdAction = (projectId) => async (dispatch) => {
  try {
    const res = await removeProjectListingById(projectId);

    dispatch({
      type: DELETE_PROJECT_BY_ID,
      payload: {
        id: projectId,
      },
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const submitReview = (projectId, body) => async (dispatch) => {
  try {
    const res = await reviewProjectListingByID(projectId, body);

    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const rejectListing = (projectId) => async (dispatch) => {
  try {
    const res = await rejectListingById(projectId);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

module.exports = {
  addNewProjectListing,
  approveProject,
  getListingsPendingApproval,
  submitReview,
  editProjectListingById,
  rejectListing,
  addValueEstimation: addValueEstimationAction,
  createProjectListingByUserID: createProjectListingByUserIDAction,
  getProjectListingsByUserID: getProjectListingsByUserIDAction,
  deleteProjectById: deleteProjectByIdAction,
  getAllProjectListings: getAllProjectListingsAction,
  assignRestoration: assignRestorationByIdAction,
  postItemForSale: postItemForSaleByIdAction,
  finishRestoration: finishRestorationByIdAction,
  startAuction: startAuctionByIdAction,
};
