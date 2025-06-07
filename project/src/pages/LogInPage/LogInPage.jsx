import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import axios from 'axios';
import styles from "../../components/Style.module.css";
import { AuthContext } from "../../components/AuthContext/AuthContext";

const LogInPage = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();    
    const { login } = useContext(AuthContext); 

    const handleLogin = async (e) => {
        e.preventDefault();

        if (username.length === 0) {
            setMessage("Колдонуучунун атын киргизиңиз");     
            return;           
        }

        if (password.length === 0) {
            setMessage("Купуя белгиңизди киргизиңиз");     
            return;           
        }

        try {
            const res = await axios.post("http://localhost:8080/auth/login", { username, password });

            const token = res.data.accessToken || res.data.token;

            if (token) {
                localStorage.setItem("access_token", token);
                localStorage.setItem("username", username);
                login(); 
                navigate("/");
            }

        } catch (error) {
            console.log(error);
            setMessage("Колдонуучунун аты же купуя белги туура эмес");
        }
    };

    return (
        <div className={styles.container}>
            <h1>Кош келиңиз</h1>

            <form onSubmit={handleLogin}>
                <div className={styles.inputStyle}>
                    <input 
                        onChange={(e) => setUsername(e.target.value)}
                        value={username}
                        type="text"
                        placeholder="Колдонуучунун аты"
                        className={styles.inputField}  
                    />  
                </div>
                <div className={styles.inputStyle}>
                    <input 
                        onChange={(e) => setPassword(e.target.value)}
                        value={password}
                        type="password"
                        placeholder="Купуя белги"
                        className={styles.inputField}  
                    /> 
                </div>
                { message && <div className={styles.error}>{message}</div>}
                <button type="submit" className={styles.btnStyle}>Кирүү</button> 
                <p className={styles.underBtn}>Эсептик жазууңуз жокпу? <br /><a href="/register" >Каттоо</a></p> 
            </form>
        </div>
    )
}

export default LogInPage;
