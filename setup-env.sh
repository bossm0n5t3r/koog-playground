#!/bin/bash

# .env 파일 생성 및 설정을 위한 스크립트

ENV_FILE=".env"
EXAMPLE_FILE=".env.example"

echo "=== Environment Variable Setup ==="

if [ ! -f "$EXAMPLE_FILE" ]; then
    echo "Error: $EXAMPLE_FILE file not found."
    exit 1
fi

# 기존 .env 파일이 있으면 백업 여부 확인
if [ -f "$ENV_FILE" ]; then
    read -p ".env file already exists. Do you want to overwrite it? (y/n): " overwrite
    if [[ ! $overwrite =~ ^[Yy]$ ]]; then
        echo "Setup cancelled."
        exit 0
    fi
    cp "$ENV_FILE" "${ENV_FILE}.bak"
    echo "Backup created: ${ENV_FILE}.bak"
fi

# .env 파일 초기화 (또는 새로 생성)
> "$ENV_FILE"

# .env.example에서 키들을 읽어와서 사용자 입력 받기
# read -u 3를 사용하여 stdin(사용자 입력)과 파일 입력을 분리
while IFS= read -r line <&3 || [ -n "$line" ]; do
    # 주석이나 빈 줄 무시
    if [[ -z "$line" || "$line" =~ ^# ]]; then
        echo "$line" >> "$ENV_FILE"
        continue
    fi

    # 키와 기본값 분리 (첫 번째 = 기준)
    if [[ "$line" == *"="* ]]; then
        key="${line%%=*}"
        example_value="${line#*=}"

        # 현재 환경 변수에서 값 확인 (간접 참조 ${!key})
        current_env_value="${!key}"

        if [ -n "$current_env_value" ]; then
            default_value="$current_env_value"
            prompt_msg="Setting for $key (Current env: $default_value)"
        else
            default_value="$example_value"
            prompt_msg="Setting for $key (Default: $default_value)"
        fi

        echo "$prompt_msg"
        # stdin에서 사용자 입력 받기
        read -p "Enter value (leave blank to use current/default): " user_value

        if [ -z "$user_value" ]; then
            final_value="$default_value"
        else
            # 사용자가 $OPENAI_API_KEY 처럼 입력했을 경우를 위해 eval/확장 처리
            # 하지만 보안상 위험할 수 있으므로 간단한 변수 확장만 시도
            if [[ "$user_value" == \$* ]]; then
                var_name="${user_value#\$}"
                final_value="${!var_name}"
                if [ -z "$final_value" ]; then
                    echo "Warning: Environment variable $user_value is not set. Using raw input."
                    final_value="$user_value"
                fi
            else
                final_value="$user_value"
            fi
        fi

        echo "$key=$final_value" >> "$ENV_FILE"
    else
        echo "$line" >> "$ENV_FILE"
    fi
done 3< "$EXAMPLE_FILE"

echo "---------------------------------"
echo ".env file has been successfully created/updated."
echo "=== Setup Complete ==="
