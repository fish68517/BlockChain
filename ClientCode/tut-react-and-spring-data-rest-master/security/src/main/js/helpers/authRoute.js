const React = require('react');
const { Route, Redirect } = require('react-router');

export default function AuthRoute({ component: Component, currentUser }) {
  return (
    <Route {...rest} render={props => {
      if (!currentUser) {
          return <Redirect to={{ pathname: '/login', state: { from: props.location } }} />;
      }

      return <Component {...props} />;
    }} />
  );
}
