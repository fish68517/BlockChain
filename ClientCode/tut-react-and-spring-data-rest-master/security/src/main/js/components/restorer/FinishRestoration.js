const React = require('react');
const { useState, useEffect } = require('react');

function FinishRestoration({ listing, isEditable, onFinishRestoration}) {
  const [message, setMessage] = useState('');
  const [error, setError] = useState(false);


  useEffect(() => {
    let timeout;
    if (message !== '') {
      timeout = setTimeout(() => setMessage(''), 2000);
    }

    return () => clearTimeout(timeout);
  }, [message]);

  const onFinish = () => {
    onFinishRestoration()
    .then(() => {
      setMessage('Restoration finished!');
    })
    .catch(() => {
      setError(true);
      setMessage('Error finishing restoration.');
    });
  }

  return (
    <div>
      {isEditable &&
        <>
          <button className="btn btn-primary d-inline-block mt-1" onClick={onFinish}>Finish Restoration</button>
        </>
      }
      {message &&
        <div className={`d-inline-block mt-2 ml-2 p-2 alert alert-${error ? 'error' : 'success'}`}>{message}</div>
      }
    </div>
  )
}


module.exports = FinishRestoration;
