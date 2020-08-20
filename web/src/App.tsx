import React from 'react'
import logo from './logo.svg'
import { BrowserRouter, Switch, Route, Redirect } from 'react-router-dom'

import View_Home from "./view/Home"
import View_Feed from "./view/Feed"

function App() {
  return (
    <BrowserRouter>
      <Switch>
        <Route path="/" exact component={View_Home} />
        <Route path="/feed" component={View_Feed} />
        <Redirect path="*" to="/" />
      </Switch>
    </BrowserRouter>
  )
}

export default App
