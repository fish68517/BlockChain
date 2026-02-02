const React = require('react');
const ReactDOM = require('react-dom');
const { connect } = require('react-redux');
const { Card, CardGroup } = require('react-bootstrap');


const InvestedProjectListing = require('./InvestedProjectListing');


const InvestedProjectListingList = ({ onInvest, Investments }) => {
  console.log('list', Investments);
  return (
      <CardGroup style={{ display: 'flex', flex: '1 0 25%', flexWrap: 'wrap', alignItems: 'flex-start' }}>
        {Investments.map(Investment =>
          <div key={Investment.listing.id} style={{ flex: '0 0 25%', maxWidth: '25%', margin: '0px' }}>
          <InvestedProjectListing
            key={Investment.listing.id}
            onInvest={onInvest}
            Investment={Investment}
          />
          </div>
        )}
      </CardGroup>
  )
}

module.exports = InvestedProjectListingList
