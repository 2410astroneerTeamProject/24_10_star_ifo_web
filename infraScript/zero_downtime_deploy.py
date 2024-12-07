#!/usr/bin/env python3

import os
import requests
import time
from typing import Optional


class ServiceManager:
    def __init__(self, socat_port: int = 8081, sleep_duration: int = 3) -> None:
        self.socat_port: int = socat_port
        self.sleep_duration: int = sleep_duration
        self.services = ["app1", "app2"]
        self.current_service: Optional[str] = None
        self.next_service: Optional[str] = None

    # 이미지 가져오기
    def _pull_latest_image(self) -> None:
        print("Pulling the latest image...")
        os.system(
            "docker pull ghcr.io/24-10-astroneer-team-project/24_10_star_info_web:latest"
        )

    # 현재 실행 중인 서비스를 확인
    def _find_current_service(self) -> None:
        cmd = f"ps aux | grep 'socat -t0 TCP-LISTEN:{self.socat_port}' | grep -v grep | awk '{{print $NF}}'"
        current_service = os.popen(cmd).read().strip()
        if current_service:
            self.current_service = (
                "app2" if "8083" in current_service else "app1"
            )
        else:
            self.current_service = "app2"

    # 다음 실행할 서비스를 확인
    def _find_next_service(self) -> None:
        self.next_service = "app1" if self.current_service == "app2" else "app2"

    # Docker Compose 서비스 재시작
    def _restart_service(self, service_name: str) -> None:
        print(f"Restarting {service_name}...")
        os.system(f"docker-compose up -d {service_name}")

    # 서비스 상태 확인
    def _is_service_up(self, service_name: str) -> bool:
        port = 8082 if service_name == "app1" else 8083
        url = f"http://127.0.0.1:{port}/actuator/health"
        try:
            response = requests.get(url, timeout=5)
            return response.status_code == 200 and response.json().get("status") == "UP"
        except requests.RequestException:
            return False

    # 포트 전환
    def _switch_port(self) -> None:
        print("Switching socat port...")
        os.system(f"pkill -f 'socat -t0 TCP-LISTEN:{self.socat_port}'")
        time.sleep(5)
        target_port = 8082 if self.next_service == "app1" else 8083
        os.system(
            f"nohup socat -t0 TCP-LISTEN:{self.socat_port},fork,reuseaddr TCP:localhost:{target_port} &>/dev/null &"
        )

    # 서비스 업데이트
    def update_service(self) -> None:
        # 1. 최신 이미지 가져오기
        self._pull_latest_image()

        # 2. 현재/다음 서비스 확인
        self._find_current_service()
        self._find_next_service()

        # 3. 다음 서비스 재시작
        self._restart_service(self.next_service)

        # 4. 서비스 상태 확인
        while not self._is_service_up(self.next_service):
            print(f"Waiting for {self.next_service} to be 'UP'...")
            time.sleep(self.sleep_duration)

        # 5. 포트 전환
        self._switch_port()

        print(f"Service switched from {self.current_service} to {self.next_service}!")


if __name__ == "__main__":
    manager = ServiceManager()
    manager.update_service()
