const React = require('react');
const { useState, useEffect } = require('react');

function StartAuction({ listing, isEditable, onStartAuction}) {
  const [message, setMessage] = useState('');
  const [error, setError] = useState(false);


  useEffect(() => {
    let timeout;
    if (message !== '') {
      timeout = setTimeout(() => setMessage(''), 2000);
    }

    return () => clearTimeout(timeout);
  }, [message]);

  const onAuction = () => {
    onStartAuction()
    .then(() => {
      setMessage('Sale auction started for item!');
    })
    .catch(() => {
      setError(true);
      setMessage('Error starting sale auction for item.');
    });
  }

  return (
    <div>
      {isEditable &&
        <>
          <button className="btn btn-primary d-inline-block mt-1" onClick={onAuction}>Start Auction</button>
        </>
      }
      {message &&
        <div className={`d-inline-block mt-2 ml-2 p-2 alert alert-${error ? 'error' : 'success'}`}>{message}</div>
      }
    </div>
  )
}


module.exports = StartAuction;
