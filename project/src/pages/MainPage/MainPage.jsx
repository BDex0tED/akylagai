import { useContext } from "react";
import { AuthContext } from "../../components/AuthContext/AuthContext";
import { useNavigate } from "react-router-dom";
import styles from "../../components/Style.module.css";
import StudyForm from "../../components/StudyForm/StudyForm";

function MainPage() {
  const navigate = useNavigate();
  const { isLoggedIn } = useContext(AuthContext);

  return (
    <div>
      {isLoggedIn ? (
        <div className={styles.pageLayout}>
          <StudyForm />
        </div>
      ) : (
        <div className={styles.container}>
          <h1>Кирүү үчүн аккаунтуңузга кириңиз</h1>
          <p>Эгер аккаунтуңуз жок болсо,<a href="/register"> катталыңыз</a></p>
          <div className={styles.lspacing}></div>
          <div className={styles.lspacing}></div>
          <div style={{width: "200px"}}>
            <button onClick={() => navigate("/login")} className={styles.btnStyle}>Кирүү</button> 
          </div> 
        </div>
      )}
    </div>
  );
}

export default MainPage