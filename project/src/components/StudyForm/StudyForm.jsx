import { useState, useEffect } from "react";
import axios from "axios";
import style from "../StudyForm/StudyForm.module.css";
import SelectBox from "../SelectBox/SelectBox";
import Button from "../Button/Button";
import TextArea from "../TextArea/TextArea";
import ChatBox from "../ChatBox/ChatBox";
import ChatHistoryPanel from "../ChatHistoryPanel/ChatHistoryPanel";
import Header from "../../components/Header/Header";

export default function StudyForm() {
  const [mode, setMode] = useState("ask");

  const [askData, setAskData] = useState({
    language: "Кыргызча",
    typeOfResponse: "Полный",
    responseMode: "",
    prompt: "",
  });
  const [checkAnswerData, setCheckAnswerData] = useState({
    userQuestion: "",
    userAnswer: "",
    language: "Кыргызча",
  });
  const [testUserData, setTestUserData] = useState({
    questionsTopic: "",
    language: "Кыргызча",
    questionsNum: "10",
    difficulty: "",
  });

  const [messages, setMessages] = useState([]);
  const [currentSessionId, setCurrentSessionId] = useState(null);
  const [sessions, setSessions] = useState([]);
  const token = localStorage.getItem("access_token");

  useEffect(() => {
    axios
        .get("http://localhost:8080/api/chat/sessions", {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => setSessions(res.data))
        .catch((err) =>
            console.error("Сессияларды алуу учурунда ката кетти", err)
        );
  }, [token]);

  const handleSelectSession = async (sessionId) => {
    setCurrentSessionId(sessionId);

    try {
      const res = await axios.get(
          `http://localhost:8080/api/chat/history/${sessionId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
      );

      const history = res.data.map((msg) => ({
        role: msg.role,
        text: msg.content,
      }));

      setMessages(history);
    } catch (err) {
      console.error("Баарлашуулардын тизмесин алуу учурунда ката кетти", err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      let sessionId = currentSessionId;

      if (!sessionId) {
        const sessionTitle =
            mode === "ask"
                ? askData.prompt.slice(0, 30)
                : mode === "checkAnswer"
                    ? checkAnswerData.userQuestion.slice(0, 30)
                    : mode === "testUser"
                        ? testUserData.questionsTopic.slice(0, 30)
                        : "Жаңы сессия";

        const sessionRes = await axios.post(
            "http://localhost:8080/api/chat/startSession",
            { title: sessionTitle || "Жаңы сессия" },
            {
              headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
              },
            }
        );

        sessionId = sessionRes.data;
        setCurrentSessionId(sessionId);

        const newSession = {
          id: sessionId,
          title: sessionTitle || "Жаңы сессия",
        };

        setSessions((prev) => [...prev, newSession]);
      }

      let dto = {};
      let endpoint = "";

      if (mode === "ask") {
        endpoint = "/api/chat/ask";
        dto = {
          language: askData.language,
          typeOfResponse: askData.typeOfResponse,
          responseMode: askData.responseMode,
          prompt: askData.prompt,
          sessionId,
        };
        setMessages((prev) => [...prev, { role: "user", text: askData.prompt }]);
        setAskData((prev) => ({ ...prev, prompt: "" }));
      } else if (mode === "checkAnswer") {
        endpoint = "/api/chat/checkAnswer";
        dto = {
          userQuestion: checkAnswerData.userQuestion,
          userAnswer: checkAnswerData.userAnswer,
          language: checkAnswerData.language,
          sessionId,
        };
        setMessages((prev) => [
          ...prev,
          {
            role: "user",
            text: `Суроо: ${checkAnswerData.userQuestion}\nЖооп: ${checkAnswerData.userAnswer}`,
          },
        ]);
        setCheckAnswerData({ userQuestion: "", userAnswer: "", language: "Кыргызча" });
      } else if (mode === "testUser") {
        endpoint = "/api/chat/testUser";
        dto = {
          questionsTopic: testUserData.questionsTopic,
          language: testUserData.language,
          questionsNum: testUserData.questionsNum,
          difficulty: testUserData.difficulty,
          sessionId,
        };
        setMessages((prev) => [
          ...prev,
          {
            role: "user",
            text: `Суроонун багыты: ${testUserData.questionsTopic}, Саны: ${testUserData.questionsNum}, Татаалдыгы: ${testUserData.difficulty}`,
          },
        ]);
        setTestUserData({
          questionsTopic: "",
          language: "Кыргызча",
          questionsNum: "",
          difficulty: "",
        });
      } else {
        alert("Выберите режим");
        return;
      }

      const res = await axios.post(`http://localhost:8080${endpoint}`, dto, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      setMessages((prev) => [...prev, { role: "ai", text: res.data }]);
    } catch (err) {
      console.error(err);
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: "Суроо жиберүү учурунда ката кетти" },
      ]);
    }
  };

  const handleMakeSimplier = async () => {
    if (!currentSessionId) {
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: "Активдүү сессия жок. Сурооңузду жибериңиз" },
      ]);
      return;
    }

    try {
      const res = await axios.post(
          `http://localhost:8080/api/chat/makeSimplier?sessionId=${currentSessionId}`,
          {},
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
      );

      setMessages((prev) => [...prev, { role: "ai", text: res.data }]);
    } catch (err) {
      console.error("Жоопту жөнөкөйлөтүүдө ката кетти:", err);
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: "Жоопту жөнөкөйлөтүүдө ката кетти." },
      ]);
    }
  };

  return (
      <div>
        <div className={style.pageContainer}>
          <div className={style.dflex}>
            <ChatHistoryPanel
                sessions={sessions}
                onSelect={handleSelectSession}
                currentSessionId={currentSessionId}
            />
            <div className={style.container}>
              <ChatBox messages={messages} />
              <form onSubmit={handleSubmit}>
                <div className={style.askBlock}>
                  <div>
                    <SelectBox
                        label="Жооптун түрү"
                        options={[
                          { label: "Суроо", value: "ask" },
                          { label: "Жоопту текшерүү", value: "checkAnswer" },
                          { label: "Колдонуучуну текшерүү", value: "testUser" },
                        ]}
                        value={mode}
                        onChange={setMode}
                    />
                    <div className={style.lspacing}></div>

                    {mode === "ask" && (
                        <>
                          <div className={style.selectBlock3}>
                            <SelectBox
                                label="Жооптун тили"
                                options={["Русский", "Кыргызча", "English"]}
                                value={askData.language}
                                onChange={(val) =>
                                    setAskData({ ...askData, language: val })
                                }
                                className={style.SelectBox}
                            />
                            <SelectBox
                                label="Суроонун түрү"
                                options={["Толук", "Орточо", "Кыска"]}
                                value={askData.typeOfResponse}
                                onChange={(val) =>
                                    setAskData({ ...askData, typeOfResponse: val })
                                }
                                className={style.SelectBox}
                            />
                            <SelectBox
                                label="Жооптун режими"
                                options={["Экзаменге даярдануу", "Теманы түшүндүрүү"]}
                                value={askData.responseMode}
                                onChange={(val) =>
                                    setAskData({ ...askData, responseMode: val })
                                }
                                className={style.SelectBox}
                            />
                            <div className={style.b}>
                              <Button
                                  text="Жооптуу жөнөкөйлөтүү"
                                  type="button"
                                  onClick={handleMakeSimplier}
                              />
                            </div>
                          </div>
                          <div className={style.spacing}></div>
                          <TextArea
                              placeholder="Cуроону жазыңыз..."
                              value={askData.prompt}
                              onChange={(e) =>
                                  setAskData({ ...askData, prompt: e.target.value })
                              }
                              rows={3}
                          />
                        </>
                    )}

                    {mode === "checkAnswer" && (
                        <>
                          <SelectBox
                              label="Тил"
                              options={["Русский", "Кыргызча"]}
                              value={checkAnswerData.language}
                              onChange={(val) =>
                                  setCheckAnswerData({ ...checkAnswerData, language: val })
                              }
                          />
                          <div className={style.lspacing}></div>
                          <TextArea
                              placeholder="Колдонуучунун суроосу"
                              value={checkAnswerData.userQuestion}
                              onChange={(e) =>
                                  setCheckAnswerData({
                                    ...checkAnswerData,
                                    userQuestion: e.target.value,
                                  })
                              }
                              rows={2}
                          />
                          <TextArea
                              placeholder="Колдонуучунун жообу"
                              value={checkAnswerData.userAnswer}
                              onChange={(e) =>
                                  setCheckAnswerData({
                                    ...checkAnswerData,
                                    userAnswer: e.target.value,
                                  })
                              }
                              rows={2}
                          />
                        </>
                    )}

                    {mode === "testUser" && (
                        <>
                          <div className={style.selectBlock}>
                            <SelectBox
                                label="Тил"
                                options={["Русский", "Кыргызча"]}
                                value={testUserData.language}
                                onChange={(val) =>
                                    setTestUserData({ ...testUserData, language: val })
                                }
                            />
                            <SelectBox
                                label="Татаалдыгы"
                                options={["Оңой", "Орточо", "Оор"]}
                                value={testUserData.difficulty}
                                onChange={(val) =>
                                    setTestUserData({
                                      ...testUserData,
                                      difficulty: val,
                                    })
                                }
                            />
                            <div className={style.numberInputWrapper}>
                              <label className={style.label}>
                                <p>Суроолордун саны</p>
                              </label>
                              <input
                                  type="number"
                                  min="1"
                                  max="100"
                                  value={testUserData.questionsNum}
                                  onChange={(e) =>
                                      setTestUserData({
                                        ...testUserData,
                                        questionsNum: e.target.value,
                                      })
                                  }
                                  className={style.numberInput}
                                  placeholder="Сан киргизиңиз"
                              />
                            </div>
                          </div>
                          <div className={style.lspacing}></div>
                          <TextArea
                              placeholder="Суроонун багыты..."
                              value={testUserData.questionsTopic}
                              onChange={(e) =>
                                  setTestUserData({
                                    ...testUserData,
                                    questionsTopic: e.target.value,
                                  })
                              }
                              rows={2}
                          />
                        </>
                    )}
                    <div className={style.spacing}></div>
                    <Button text="Жөнөтүү" type="submit" />
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
  );
}
