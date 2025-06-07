import { RouterProvider } from "react-router-dom";
import { routers } from "./app/routers";
import { AuthProvider } from "../src/components/AuthContext/AuthContext";
import './App.css'

function App() {

  return (
    <>
       <AuthProvider>
        <RouterProvider router={routers} />
       </AuthProvider>
       
    </>
  )
}

export default App
