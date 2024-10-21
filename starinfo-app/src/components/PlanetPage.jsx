import React, {useEffect, useState} from 'react';
import anime from 'animejs/lib/anime.es.js';
import SolarSystem from './SolarSystem';
import './Planet.css';

const PlanetPage = () => {
    const [bodyClass, setBodyClass] = useState('opening hide-UI view-2D zoom-large data-close controls-close');
    const [solarSystemClass, setSolarSystemClass] = useState('earth');
    const [isDataOpen, setIsDataOpen] = useState(false);
    // const [isControlsOpen, setIsControlsOpen] = useState(false);

    useEffect(() => {
        const init = () => {
            setTimeout(() => {
                setBodyClass('view-3D set-speed');
                // setSolarSystemClass('earth');
                // 화면 크기에 따라 처음 데이터 패널 열기 설정
                if (window.innerWidth >= 1000) {
                    setIsDataOpen(true);  // 큰 화면일 때만 열기
                }
                anime({
                    targets: '#solar-system .planet',
                    translateX: (el) => el.dataset.x,
                    translateY: (el) => el.dataset.y,
                    easing: 'easeInOutQuad',
                    duration: 2000,
                });
            }, 2000);
        };
        init();

        // 윈도우 resize 이벤트 핸들러 추가
        const handleResize = () => {
            if (window.innerWidth < 1000) {  // 특정 크기 이하일 때
                setIsDataOpen(false);  // 데이터를 자동으로 닫기
            } else {
                setIsDataOpen(true);   // 큰 화면에서는 다시 열기
            }
        };

        window.addEventListener('resize', handleResize);  // 리사이즈 이벤트 감지

        // 컴포넌트가 unmount될 때 이벤트 리스너를 제거
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);

    const handlePlanetClick = (planetClass) => {
        setSolarSystemClass(planetClass);
        anime({
            targets: `#solar-system.${planetClass} .planet`,
            scale: [1, 1],
            duration: 1000,
            easing: 'easeInOutQuad',
        });
    };

    return (
        <div className={`planet-container ${bodyClass}`}>
            <div id="navbar">
                <a id="toggle-data" href="#data" onClick={(e) => {
                    e.preventDefault();
                    setIsDataOpen(!isDataOpen);
                }}>Data</a>
                {/*<a id="toggle-controls" href="#controls" onClick={(e) => {*/}
                {/*    e.preventDefault();*/}
                {/*    setIsControlsOpen(!isControlsOpen);*/}
                {/*}}>Controls</a>*/}
            </div>

            {isDataOpen && (
                <div id="data">
                    <a className={`sun ${solarSystemClass === 'sun' ? 'active' : ''}`} onClick={() => handlePlanetClick('sun')} href="#sunspeed">Sun</a>
                    <a className={`mercury ${solarSystemClass === 'mercury' ? 'active' : ''}`} onClick={() => handlePlanetClick('mercury')} href="#mercuryspeed">Mercury</a>
                    <a className={`venus ${solarSystemClass === 'venus' ? 'active' : ''}`} onClick={() => handlePlanetClick('venus')} href="#venusspeed">Venus</a>
                    <a className={`earth ${solarSystemClass === 'earth' ? 'active' : ''}`} onClick={() => handlePlanetClick('earth')} href="#earthspeed">Earth</a>
                    <a className={`mars ${solarSystemClass === 'mars' ? 'active' : ''}`} onClick={() => handlePlanetClick('mars')} href="#marsspeed">Mars</a>
                    <a className={`jupiter ${solarSystemClass === 'jupiter' ? 'active' : ''}`} onClick={() => handlePlanetClick('jupiter')} href="#jupiterspeed">Jupiter</a>
                    <a className={`saturn ${solarSystemClass === 'saturn' ? 'active' : ''}`} onClick={() => handlePlanetClick('saturn')} href="#saturnspeed">Saturn</a>
                    <a className={`uranus ${solarSystemClass === 'uranus' ? 'active' : ''}`} onClick={() => handlePlanetClick('uranus')} href="#uranusspeed">Uranus</a>
                    <a className={`neptune ${solarSystemClass === 'neptune' ? 'active' : ''}`} onClick={() => handlePlanetClick('neptune')} href="#neptunespeed">Neptune</a>
                </div>
            )}

            {/*{isControlsOpen && (*/}
            {/*    <div id="controls">*/}
            {/*        <label className="set-view"><input type="checkbox"/> View</label>*/}
            {/*        <label className="set-zoom"><input type="checkbox"/> Zoom</label>*/}
            {/*        <label><input type="radio" className="set-speed" name="scale" defaultChecked/> Speed</label>*/}
            {/*        <label><input type="radio" className="set-size" name="scale"/> Size</label>*/}
            {/*        <label><input type="radio" className="set-distance" name="scale"/> Distance</label>*/}
            {/*    </div>*/}
            {/*)}*/}

            <div id="universe" className="scale-stretched">
                <div id="galaxy">
                    <SolarSystem solarSystemClass={solarSystemClass} handlePlanetClick={handlePlanetClick} />
                </div>
            </div>
        </div>
    );
};

export default PlanetPage;