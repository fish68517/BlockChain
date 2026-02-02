const React = require('react');
import { history } from '../helpers/history';

const parseJwt = (token) => {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    return null;
  }
};

class AuthVerify extends React.Component {
  constructor(props) {
    super(props);

    history.listen(() => {
      const user = JSON.parse(localStorage.getItem("user"));

      if (user) {
        const decodedJwt = parseJwt(user.accessToken);

        if (decodedJwt.exp * 1000 < Date.now()) {
          props.logOut();
        }
      }
    });
  }

  render() {
    return <div></div>;
  }
}

module.exports = AuthVerify;