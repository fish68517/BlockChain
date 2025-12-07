const axios = require("axios");
const createBaseRequest = require("../common/http-common");
const { getAuthHeader } = require("../utils/auth");

const getAllProjectListings = () => {
  return createBaseRequest().get("/projectListings");
};

const getProjectListing = (id) => {
  return createBaseRequest().get(`/projectListings/${id}`);
};

const getProjectListingsByUserID = (userID, size, number) => {
  const params = {
    page: number,
    size,
  };
  console.log("params", params);
  return createBaseRequest().get(`/users/${userID}/projectListings`, {
    params,
  });
};

const createProjectListingByUserID = (userID, data) => {
  return axios.post(
    `http://localhost:8090/api/users/${userID}/projectListings`,
    data,
    {
      headers: {
        "Content-Type": "multipart/form-data",
        ...getAuthHeader(),
      },
    }
  );
};

const editProjectListing = (listingId, data) => {
  return axios.put(
    `http://localhost:8090/api/projectListings/${listingId}`,
    data,
    {
      headers: {
        "Content-Type": "multipart/form-data",
        ...getAuthHeader(),
      },
    }
  );
};

const createProjectListingByUserName = (username, data) => {
  return createBaseRequest().post(`/users/${username}/projectListings`, data);
};

const updateProjectListingById = (id, data) => {
  return createBaseRequest().put(`/projectListings/${id}`, data);
};

const removeProjectListingById = (id) => {
  return createBaseRequest().delete(`/projectListings/${id}`);
};

const removeAllByUserID = (userID) => {
  return createBaseRequest().delete(`/users/${userID}/projectListings`);
};

const reviewProjectListingByID = (id, data) => {
  return createBaseRequest().put(`/projectListings/${id}/review`, data);
};

const addValueEstimation = (projectListingID, data) => {
  return createBaseRequest().post(
    `/projectListings/${projectListingID}/valueEstimation`,
    data
  );
};

const rejectListingById = (listingId) => {
  return createBaseRequest().post(`/projectListings/${listingId}/reject`);
};

const assignRestorationById = (listingId) => {
  return createBaseRequest().post(
    `/projectListings/${listingId}/assignRestoration`
  );
};

const finishRestorationById = (listingId) => {
  return createBaseRequest().post(
    `/projectListings/${listingId}/finishRestoration`
  );
};

const postItemForSaleById = (listingId) => {
  return createBaseRequest().post(
    `/projectListings/${listingId}/postItemForSale`
  );
};

const startAuctionById = (listingId) => {
  return createBaseRequest().post(`/projectListings/${listingId}/startAuction`);
};

const getListingsPendingBid = () => {
  return createBaseRequest().get("/projectListings/restoration");
};

const getListingsPendingAuctionBid = () => {
  return createBaseRequest().get("/projectListings/buyer");
};

const ProjectListingsService = {
  getAllProjectListings,
  getProjectListing,
  getProjectListingsByUserID,
  createProjectListingByUserID,
  createProjectListingByUserName,
  removeAllByUserID,
  reviewProjectListingByID,
  updateProjectListingById,
  removeProjectListingById,
  removeAllByUserID,
  addValueEstimation,
  editProjectListing,
  rejectListingById,
  getListingsPendingBid,
  assignRestorationById,
  postItemForSaleById,
  finishRestorationById,
  getListingsPendingAuctionBid,
  startAuctionById,
};

module.exports = ProjectListingsService;
