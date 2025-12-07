const React = require('react');
const { useState, useEffect } = require('react');

function AssignRestoration({ listing, isEditable, onAssignRestoration}) {
  const [message, setMessage] = useState('');
  const [error, setError] = useState(false);


  useEffect(() => {
    let timeout;
    if (message !== '') {
      timeout = setTimeout(() => setMessage(''), 2000);
    }

    return () => clearTimeout(timeout);
  }, [message]);

  const onAssign = () => {
    onAssignRestoration()
    .then(() => {
      setMessage('Restoration assigned!');
    })
    .catch(() => {
      setError(true);
      setMessage('Error assigning restoration.');
    });
  }

  return (
    <div>
      {isEditable &&
        <>
          <button className="btn btn-primary d-inline-block mt-1" onClick={onAssign}>Assign Restoration</button>
        </>
      }
      {message &&
        <div className={`d-inline-block mt-2 ml-2 p-2 alert alert-${error ? 'error' : 'success'}`}>{message}</div>
      }
    </div>
  )
}


module.exports = AssignRestoration;
