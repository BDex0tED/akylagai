import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from 'axios';
import styles from "../../components/Style.module.css";

const RegisterPage = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const Register = async (e) => {
        e.preventDefault();

        if(username.length == 0){
            setMessage("Колдонуучунун атын киргизиңиз");     
            return;           
        }

        if(email.length == 0){
            setMessage("Электрондук почта киргизиңиз ");     
            return;           
        }

        if(password.length < 6){
            setMessage("Купуя белги жок дегенде 6 белгиден турушу");     
            return;           
        }

        try {  
            const res = await axios.post("http://localhost:8080/auth/register", { username, password, email});

            navigate("/login");

        } catch (error) {
            console.log(error);
            setMessage("Бул логин мурун катталган");
        }
    };


    return (
        <div className={styles.container}>
            <h1>Каттоо эсебин түзүү</h1>

            <div >
                <form onSubmit={Register}>
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
                            onChange={(e) => setEmail(e.target.value)}
                            value={email}
                            type="text"
                            placeholder="Электрондук почта "
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
                    <button type="submit" className={styles.btnStyle}>Катталуу</button> 
                    <p className={styles.underBtn}>Сиздин эсептик жазууңуз барбы? <a href="/login" > Кирүү</a></p> 
                </form>
            </div>

        </div>
    )

}

export default RegisterPage
