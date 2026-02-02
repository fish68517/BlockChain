var path = require("path");

module.exports = {
  entry: "./src/main/js/index.js",
  devtool: "source-map",
  cache: true,
  mode: "development",
  resolve: {
    alias: {
      stompjs: __dirname + "/node_modules" + "/stompjs/lib/stomp.js",
    },
  },
  output: {
    path: path.resolve(__dirname, "src/main/resources/static/built"),
    filename: "bundle2.js",
  },
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        use: ["babel-loader"],
        type: "javascript/auto",
      },
      {
        test: /\.css$/i,
        use: ["css-loader", "style-loader"],
      },
    ],
  },
  externals: {
    // global app config object
    config: JSON.stringify({
      apiUrl: "http://localhost:8090",
      assetsUrl: "http://localhost:8090/assets/",
      uploadsUrl: "http://localhost:8090/assets/uploads",
    }),
  },
};
